(function () {
    'use strict';

    angular
        .module('app')
        .factory('MatchingService', MatchingService);

    MatchingService.$inject = ['$http'];
    function MatchingService($http) {
        var service = {};

        service.SavePlanResourceTeamMemberMatching = SavePlanResourceTeamMemberMatching;

        return service;

        //save the resource-Trello user association
        function SavePlanResourceTeamMemberMatching(params){
            return $http.post('/match/',params).then(handleSuccess,handleError);
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