(function () {
    'use strict';

    angular
        .module('app')
        .factory('LogService', LogService);

    LogService.$inject = ['$http'];
    function LogService($http) {
        var service = {};

        service.GetBoards = GetBoards;
        service.GetAllLogs = GetAllLogs;
        service.GetBoardLogs = GetBoardLogs;
        service.LogRead = LogRead;
        service.RejectCardDone = RejectCardDone;

        return service;

        //get boards of the current user
        function GetBoards(username){
            return $http.get('/boards/?username='+username).then(handleSuccess,handleError);
        }

        //get all board logs of the current user
        function GetAllLogs(username){
            return $http.get('/logs/?username='+username).then(handleSuccess,handleError);
        }

        //get logs of the specified board
        function GetBoardLogs(username,boardId){
            return $http.get('/logs/?username='+username+'&boardId='+boardId).then(handleSuccess,handleError);
        }

        //mark log as read
        function LogRead(logId){
            return $http.put('/logs/'+logId,'read').then(handleSuccess,handleError);
        }

        //refuse card posting a comment with the reasons why this card is rejected
        function RejectCardDone(username,cardId,comment){
            //body: username and comment
            var body = {
                username:username,
                comment:comment
            }
            return $http.post('/cards/'+cardId+'/refuse',body).then(handleSuccess,handleError);
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