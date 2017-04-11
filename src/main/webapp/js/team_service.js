(function () {
    'use strict';

    angular
        .module('app')
        .factory('TeamService', TeamService);

    TeamService.$inject = ['$http'];
    function TeamService($http) {
        var service = {};

        service.GetTeams = GetTeams;
        service.GetTeamMembers = GetTeamMembers;

        return service;

        //get Trello teams names which a user belongs
        function GetTeams(username) {
            return $http.get('/teams/?username=' + username).then(handleSuccess, handleError);
        }

        function GetTeamMembers(username,teamId){
            return $http.get('/teams/members/?username=' + username + '&teamId=' + teamId)
                .then(handleSuccess, handleError);
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