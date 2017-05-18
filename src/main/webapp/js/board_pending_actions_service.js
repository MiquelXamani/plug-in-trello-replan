(function () {
    'use strict';

    angular
        .module('app')
        .factory('BoardPendingActionsService', BoardPendingActionsService);

    BoardPendingActionsService.$inject = ['$http'];
    function BoardPendingActionsService($http) {
        var service = {};

        service.GetBoards = GetBoards;
        service.GetAllLogs = GetAllLogs;
        service.GetBoardLogs = GetBoardLogs;
        service.GetCardTracking = GetCardTracking;
        service.SetAccepted = SetAccepted;
        service.RejectCardDone = RejectCardDone;
        service.Replan = Replan;

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

        function GetCardTracking(cardId){
            return $http.get('/card/tracking/'+cardId).then(handleSuccess,handleError);
        }

        //mark or unmark log as completed
        function SetAccepted(logId,accepted){
            var body ={accepted:accepted};
            return $http.post('/logs/'+logId+'/completed',body).then(handleSuccess,handleError);
        }

        //refuse card posting a comment with the reasons why this card is rejected
        function RejectCardDone(username,cardId,cardName,boardId,comment){
            //body: username and comment
            var body = {
                username:username,
                comment:comment,
                cardId:cardId,
                cardName:cardName,
                boardId:boardId
            }
            return $http.post('/logs/reject-card',body).then(handleSuccess,handleError);
        }

        function Replan(logs){
            return $http.post('/logs/replan',logs).then(handleSuccess,handleError);
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