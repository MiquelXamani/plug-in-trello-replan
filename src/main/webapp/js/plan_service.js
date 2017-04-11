(function () {
    'use strict';

    angular
        .module('app')
        .factory('PlanService', PlanService);

    PlanService.$inject = ['$http'];
    function PlanService($http) {
        var service = {};

        service.GetProjects = GetProjects;
        service.GetReleases = GetReleases;
        //service.GetPlan = GetPlan;

        return service;

        //get projects of Replan tool
        function GetProjects() {
            return $http.get('/projects/').then(handleSuccess, handleError);
        }
        //get releases of a given project
        function GetReleases(projectId){
            return $http.get('/projects/' + projectId + '/releases/').then(handleSuccess,handleError);
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