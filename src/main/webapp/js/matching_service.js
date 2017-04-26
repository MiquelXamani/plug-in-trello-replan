(function () {
    'use strict';

    angular
        .module('app')
        .factory('MatchingService', MatchingService);

    MatchingService.$inject = ['$http'];
    function MatchingService($http) {
        var service = {};

        service.SavePlanResourceTeamMemberMatchings = SavePlanResourceTeamMemberMatchings;
        service.GetMatchings = GetMatchings;
        service.DeletePlanResourceTeamMemberMatchings = DeletePlanResourceTeamMemberMatchings;

        return service;

        //get current matchings, plan, and team members and resources unmatched
        function GetMatchings(username, endpointId, projectId, releaseId, teamId){
            return $http.get('/matchings/?username='+username+"&endpointId="+endpointId+"&projectId="+projectId+"&releaseId="+releaseId+"&teamId="+teamId)
                .then(handleSuccess,handleError);
        }
        //save the resource-Trello user association
        function SavePlanResourceTeamMemberMatchings(username, params){
            return $http.post('/matchings/create-matchings/?username=' + username, params).then(handleSuccess,handleError);
        }

        function DeletePlanResourceTeamMemberMatchings(username, params){
            return $http.post('/matchings/delete-matchings/?username=' + username, params).then(handleSuccess,handleError);
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