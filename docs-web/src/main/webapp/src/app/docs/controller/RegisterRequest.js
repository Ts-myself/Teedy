'use strict';

/**
 * Register request controller.
 */
angular.module('docs').controller('RegisterRequest', function ($scope, Restangular, $dialog, $translate) {
    $scope.user = {};

    // Submit registration request
    $scope.submitRegistration = function () {
        Restangular.one('user').post('register_request', $scope.user)
            .then(function () {
                // Success message
                var title = $translate.instant('register_request.success_title');
                var msg = $translate.instant('register_request.success_message');
                var btns = [{ result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }];
                $dialog.messageBox(title, msg, btns);
                $scope.user = {}; // Reset form
            }, function (response) {
                console.error('Registration request error:', response);

                var title = $translate.instant('register_request.error_title');
                var msg = $translate.instant('register_request.error_general');

                // 扩展错误处理，处理更多可能的错误情况
                if (response.status === 400) {
                    // 请求格式错误
                    msg = $translate.instant('register_request.error_invalid_request');
                } else if (response.status === 401 || response.status === 403) {
                    // 权限问题
                    msg = $translate.instant('register_request.error_permission');
                } else if (response.status === 500) {
                    // 服务器内部错误
                    msg = $translate.instant('register_request.error_server');
                }

                // 添加详细日志
                console.error('Status code:', response.status);
                console.error('Response data:', response.data);

                // Error handling with detailed messages
                if (response.data && response.data.type) {
                    if (response.data.type === 'ValidationError') {
                        msg = response.data.message || $translate.instant('register_request.error_validation');
                    } else if (response.data.type === 'AlreadyExistingUsername') {
                        msg = $translate.instant('register_request.error_username_exists');
                    } else if (response.data.type === 'EmailAlreadyUsedException') {
                        msg = $translate.instant('register_request.error_email_exists');
                    } else {
                        // For any other error types, use the message from server if available
                        msg = response.data.message || msg;
                    }
                }

                var btns = [{ result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }];
                $dialog.messageBox(title, msg, btns);
            });
    };
});