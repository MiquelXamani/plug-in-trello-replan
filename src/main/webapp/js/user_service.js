(function () {
    'use strict';

    angular
        .module('app')
        .factory('UserService', UserService);

    UserService.$inject = ['$http'];
    function UserService($http) {
        var service = {};

        service.GetByUsername = GetByUsername;
        service.Create = Create;

        return service;

        function GetByUsername(username) {
            return $http.get('/users/?username=' + username).then(handleSuccess, handleError);
        }

        function Create(user) {
            return $http.post('/users', user).then(handleSuccess, handleError);
        }

        // private functions

        function handleSuccess(res) {
            //console.log("SUCCESS RESPONSE");
            res.data.success = true;
            return res.data;
        }

        function handleError(res) {
            //console.log(res.data.description);
            var r = {};
            r.success = false;
            r.message = res.data.description;
            return r;
        }
    }

})();