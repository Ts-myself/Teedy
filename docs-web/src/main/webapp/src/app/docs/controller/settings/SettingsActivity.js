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
     * Page change handler.
     */
    $scope.$watch('currentPage', function () {
        $scope.loadActivities();
    });

    // Load activities when controller initializes
    $scope.loadActivities();
});