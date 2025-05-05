'use strict';

/**
 * Settings register request page controller.
 */
angular.module('docs').controller('SettingsRegisterRequest', function ($scope, Restangular, $dialog, $translate) {
    /**
     * Load registration requests.
     */
    $scope.loadRequests = function () {
        Restangular.one('user/register_request').get({
            status: 'PENDING' // Only fetch pending requests
        }).then(function (data) {
            $scope.requests = data.requests;
        });
    };

    /**
     * Approve a registration request.
     */
    $scope.approveRequest = function (request) {
        Restangular.one('user/register_request', request.id).one('approve').put().then(function () {
            $scope.loadRequests();

            // Show success message
            var msg = $translate.instant('settings.register_request.approve_success');
            $scope.alerts.push({ type: 'success', msg: msg });

            // Auto-close alert after 3 seconds
            setTimeout(function () {
                $scope.$apply(function () {
                    $scope.alerts.shift();
                });
            }, 3000);
        });
    };

    /**
     * Reject a registration request.
     */
    $scope.rejectRequest = function (request) {
        Restangular.one('user/register_request', request.id).one('reject').put().then(function () {
            $scope.loadRequests();

            // Show success message
            var msg = $translate.instant('settings.register_request.reject_success');
            $scope.alerts.push({ type: 'success', msg: msg });

            // Auto-close alert after 3 seconds
            setTimeout(function () {
                $scope.$apply(function () {
                    $scope.alerts.shift();
                });
            }, 3000);
        });
    };

    // Initialize alerts array
    $scope.alerts = [];

    // Close an alert
    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };

    // Load requests when controller initializes
    $scope.loadRequests();
});