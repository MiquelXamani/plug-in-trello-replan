(function () {
    'use strict';

    angular
        .module('app')
        .factory('AlertService', AlertService);

    AlertService.$inject = ['$rootScope'];
    function AlertService($rootScope) {
        var service = {};

        service.Success = Success;
        service.Error = Error;

        initService();

        return service;

        function initService() {
            $rootScope.$on('$locationChangeStart', function () {
                clearAlertMessage();
            });

            function clearAlertMessage() {
                var alert = $rootScope.alert;
                if (alert) {
                    if (!alert.keepAfterLocationChange) {
                        delete $rootScope.alert;
                    } else {
                        // only keep for a single location change
                        alert.keepAfterLocationChange = false;
                    }
                }
            }
        }

        function Success(message, keepAfterLocationChange) {
            $rootScope.alert = {
                message: message,
                type: 'success',
                keepAfterLocationChange: keepAfterLocationChange
            };
        }

        function Error(message, keepAfterLocationChange) {
            $rootScope.alert = {
                message: message,
                type: 'error',
                keepAfterLocationChange: keepAfterLocationChange
            };
        }
    }

})();