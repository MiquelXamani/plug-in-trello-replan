(function () {
    'use strict';

    angular
        .module('app')
        .factory('BoardService', BoardService);

    BoardService.$inject = ['$http'];
    function BoardService($http) {
        var service = {};

        service.LoadPlanOnTrello = LoadPlanOnTrello;

        return service;

        //get resources of the release specified that aren't linked with any Trello user
        function LoadPlanOnTrello(params){
            return $http.post('/boards/',params).then(handleSuccess,handleError);
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