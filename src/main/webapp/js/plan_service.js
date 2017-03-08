(function () {
    'use strict';

    angular
        .module('app')
        .factory('PlanService', PlanService);

    PlanService.$inject = ['$http'];
    function PlanService($http) {
        var service = {};

        service.GetPlans = GetPlans;

        return service;

        //get plans generated by the user using Replan tool
        function GetPlans(username) {
            return $http.get('/plans/?username=' + username).then(handleSuccess, handleError);
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