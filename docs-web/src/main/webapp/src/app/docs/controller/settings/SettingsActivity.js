'use strict';

/**
 * Settings activity controller.
 */
angular.module('docs').controller('SettingsActivity', function ($scope, $state, Restangular, $filter) {
    // Initialize variables
    $scope.logs = [];
    $scope.totalActivities = 0;
    $scope.activitiesPerPage = 50;
    $scope.currentPage = 1;
    $scope.isLoading = false;
    $scope.sortColumn = 1; // Default sort by date
    $scope.asc = false; // Descending
    $scope.ganttData = []; // Gantt chart data
    $scope.showGantt = false; // Toggle for Gantt chart view
    $scope.timeRange = {
        start: null,
        end: null
    };
    $scope.maxDailyActivity = 1; // Maximum daily activity count for any user
    $scope.chartHeight = 250; // Height of the chart area
    $scope.chartPadding = 30; // Padding from top of chart

    // Define a color palette for different users
    $scope.colorPalette = [
        '#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd',
        '#8c564b', '#e377c2', '#7f7f7f', '#bcbd22', '#17becf',
        '#aec7e8', '#ffbb78', '#98df8a', '#ff9896', '#c5b0d5'
    ];

    /**
     * Load user activities.
     */
    $scope.loadActivities = function () {
        $scope.isLoading = true;

        // Calculate offset
        var offset = ($scope.currentPage - 1) * $scope.activitiesPerPage;

        // Fetch activities from API
        Restangular.one('admin/activity').get({
            offset: offset,
            limit: $scope.activitiesPerPage,
            sort_column: $scope.sortColumn,
            asc: $scope.asc
        }).then(function (data) {
            $scope.logs = data.activities;
            $scope.totalActivities = data.total;
            $scope.isLoading = false;
            $scope.processGanttData();
        }, function () {
            $scope.isLoading = false;
        });
    };

    /**
     * Sort activities.
     */
    $scope.sortActivities = function (column) {
        if ($scope.sortColumn === column) {
            $scope.asc = !$scope.asc;
        } else {
            $scope.asc = true;
        }
        $scope.sortColumn = column;
        $scope.loadActivities();
    };

    /**
     * Process activity data for Gantt chart.
     */
    $scope.processGanttData = function () {
        if (!$scope.logs || $scope.logs.length === 0) {
            $scope.ganttData = [];
            return;
        }

        // Group activities by user and day
        var groupedByUser = {};
        var activityByDay = {};
        var earliestDate = new Date();
        var latestDate = new Date(0);
        var userColorMap = {};
        var colorIndex = 0;
        $scope.maxDailyActivity = 1; // Reset max activity counter

        $scope.logs.forEach(function (activity) {
            var username = activity.username || 'Unknown';
            var activityDate = new Date(activity.create_date);

            // Format date to YYYY-MM-DD for day grouping
            var dayKey = $filter('date')(activityDate, 'yyyy-MM-dd');

            // Assign color to user if not already assigned
            if (!userColorMap[username]) {
                userColorMap[username] = $scope.colorPalette[colorIndex % $scope.colorPalette.length];
                colorIndex++;
            }

            // Initialize user entry if not exists
            if (!groupedByUser[username]) {
                groupedByUser[username] = [];
            }

            // Initialize activity by day tracking
            if (!activityByDay[username]) {
                activityByDay[username] = {};
            }

            // Count activities per user per day
            if (!activityByDay[username][dayKey]) {
                activityByDay[username][dayKey] = {
                    count: 0,
                    date: new Date(activityDate.getFullYear(), activityDate.getMonth(), activityDate.getDate()),
                    activities: []
                };
            }

            activityByDay[username][dayKey].count++;
            activityByDay[username][dayKey].activities.push({
                id: activity.id,
                date: activityDate,
                type: activity.class,
                message: activity.message,
                target: activity.target
            });

            // Track date range
            if (activityDate < earliestDate) earliestDate = activityDate;
            if (activityDate > latestDate) latestDate = activityDate;
        });

        // Fill in missing days for continuity in the chart
        Object.keys(activityByDay).forEach(function (username) {
            var userDays = Object.keys(activityByDay[username]).map(function (dayKey) {
                var dayData = activityByDay[username][dayKey];
                // Update max count if this day has more activities
                if (dayData.count > $scope.maxDailyActivity) {
                    $scope.maxDailyActivity = dayData.count;
                }
                return {
                    date: dayData.date,
                    count: dayData.count,
                    activities: dayData.activities
                };
            });

            // Sort by date
            userDays.sort(function (a, b) {
                return a.date - b.date;
            });

            // Fill in missing days in between with zero activity
            if (userDays.length > 1) {
                var filledDays = [userDays[0]];
                for (var i = 1; i < userDays.length; i++) {
                    var prevDay = userDays[i - 1];
                    var currDay = userDays[i];

                    // Check if days are consecutive
                    var dayDiff = Math.floor((currDay.date - prevDay.date) / (24 * 60 * 60 * 1000));

                    // If there's a gap, fill in the missing days
                    if (dayDiff > 1) {
                        for (var j = 1; j < dayDiff; j++) {
                            var missingDate = new Date(prevDay.date);
                            missingDate.setDate(missingDate.getDate() + j);

                            filledDays.push({
                                date: missingDate,
                                count: 0, // Zero activity for missing days
                                activities: [],
                                isMissing: true // Flag to identify missing days
                            });
                        }
                    }

                    filledDays.push(currDay);
                }

                groupedByUser[username] = filledDays;
            } else {
                groupedByUser[username] = userDays;
            }
        });

        // Add padding to date range
        earliestDate = new Date(earliestDate.getFullYear(), earliestDate.getMonth(), earliestDate.getDate());
        earliestDate.setDate(earliestDate.getDate() - 1);

        latestDate = new Date(latestDate.getFullYear(), latestDate.getMonth(), latestDate.getDate());
        latestDate.setDate(latestDate.getDate() + 1);

        $scope.timeRange.start = earliestDate;
        $scope.timeRange.end = latestDate;

        // Format data for Gantt chart
        $scope.ganttData = Object.keys(groupedByUser).map(function (username, index) {
            return {
                id: index,
                name: username,
                color: userColorMap[username],
                days: groupedByUser[username]
            };
        });

        // Sort users by total number of activities (descending)
        $scope.ganttData.sort(function (a, b) {
            var totalA = a.days.reduce(function (sum, day) { return sum + day.count; }, 0);
            var totalB = b.days.reduce(function (sum, day) { return sum + day.count; }, 0);
            return totalB - totalA;
        });
    };

    /**
     * Toggle Gantt chart view.
     */
    $scope.toggleGanttView = function () {
        $scope.showGantt = !$scope.showGantt;
    };

    /**
     * Calculate horizontal position for Gantt chart item.
     */
    $scope.getItemPosition = function (date) {
        if (!$scope.timeRange.start || !$scope.timeRange.end) return 0;

        var totalTime = $scope.timeRange.end - $scope.timeRange.start;
        var itemTime = date - $scope.timeRange.start;
        return (itemTime / totalTime) * 100;
    };

    /**
     * Calculate vertical position for activity based on count.
     * Higher count = higher position (lower y-value in SVG)
     */
    $scope.getActivityHeight = function (count) {
        // Calculate position with padding from top
        var height = $scope.chartHeight - $scope.chartPadding;
        return height - ((count / $scope.maxDailyActivity) * height) + $scope.chartPadding;
    };

    /**
     * Generate SVG path for connecting user activities
     */
    $scope.generatePath = function (days) {
        if (!days || days.length === 0) return "";

        // Filter out missing days for smoother curve
        var visibleDays = days.filter(function (day) {
            return !day.isMissing || day.count > 0;
        });

        if (visibleDays.length === 0) return "";

        // Create SVG path with smooth curve using cardinal spline
        var path = "M ";

        if (visibleDays.length === 1) {
            // If only one point, just draw a small horizontal line
            var x = $scope.getItemPosition(visibleDays[0].date) + '%';
            var y = $scope.getActivityHeight(visibleDays[0].count);
            return "M " + x + " " + y + " h 1";
        }

        // Use a curved path for multiple points
        visibleDays.forEach(function (day, index) {
            var x = $scope.getItemPosition(day.date) + '%';
            var y = $scope.getActivityHeight(day.count);

            if (index === 0) {
                path += x + " " + y;
            } else {
                // Use a smoother curve for better visualization
                path += " L " + x + " " + y;
            }
        });

        return path;
    };

    /**
     * Format date for display.
     */
    $scope.formatDate = function (date) {
        return $filter('date')(date, 'MMM d, y');
    };

    /**
     * Format activities tooltip for a day
     */
    $scope.formatDayTooltip = function (day) {
        if (!day || !day.activities || day.activities.length === 0) return "";

        var tooltipText = $filter('date')(day.date, 'MMM d, y') + ' - ' + day.count + ' activities\n';

        // Limit to first 5 activities for tooltip clarity
        var displayActivities = day.activities.slice(0, 5);
        displayActivities.forEach(function (activity) {
            tooltipText += '• ' + activity.message + '\n';
        });

        // Show indication if there are more
        if (day.activities.length > 5) {
            tooltipText += '• ... and ' + (day.activities.length - 5) + ' more';
        }

        return tooltipText;
    };

    /**
     * Page change handler.
     */
    $scope.$watch('currentPage', function () {
        $scope.loadActivities();
    });

    // Load activities when controller initializes
    $scope.loadActivities();
});