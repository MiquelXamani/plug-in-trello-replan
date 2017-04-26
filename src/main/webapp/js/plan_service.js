(function () {
    'use strict';

    angular
        .module('app')
        .factory('PlanService', PlanService);

    PlanService.$inject = ['$http'];
    function PlanService($http) {
        var service = {};

        service.GetEndpoints = GetEndpoints;
        service.GetProjects = GetProjects;
        service.GetReleases = GetReleases;

        return service;

        //get Replan endpoints
        function GetEndpoints() {
            return $http.get('/replan-endpoints/').then(handleSuccess, handleError);
        }

        //get projects of Replan tool
        function GetProjects(endpointId) {
            return $http.get('/replan-endpoints/' + endpointId +'/projects/').then(handleSuccess, handleError);
        }
        //get releases of a given project
        function GetReleases(endpointId, projectId){
            return $http.get('/replan-endpoints/' + endpointId + '/projects/' + projectId + '/releases/').then(handleSuccess,handleError);
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