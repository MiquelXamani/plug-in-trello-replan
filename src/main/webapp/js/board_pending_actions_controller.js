(function () {
    'use strict';

    angular
        .module('app')
        .controller('BoardPendingActionsController', BoardPendingActionsController);

    BoardPendingActionsController.$inject = ['$location','$rootScope','BoardPendingActionsService'];
    function BoardPendingActionsController($location,$rootScope,BoardPendingActionsService) {
        var vm = this;
        vm.boards = [];
        vm.logs = [];
        vm.comment = "";
        vm.logToRefuseIndex = "";
        vm.dataLoading = false;
        vm.cardNameTracking = "";
        vm.acceptedLogs = [];
        vm.cardMovements = [];

        vm.getLogs = getLogs;
        vm.displayCardTracking = displayCardTracking;
        vm.markAsCompleted = markAsCompleted;
        vm.reject = reject;
        vm.submitComment = submitComment;
        vm.replan = replan;

        function getAllLogs(){
            BoardPendingActionsService.GetAllLogs($rootScope.globals.currentUser.username).then(function(logs){
                vm.logs = logs;
            });
        }

        function getBoards(){
            BoardPendingActionsService.GetBoards($rootScope.globals.currentUser.username).then(function(boards){
               if(boards.length > 0){
                   vm.boards = boards;
                   var allBoards = {name:"All"};
                   vm.boards.splice(0,0,allBoards);
                   vm.selectedBoard = vm.boards[0];
                   getAllLogs();
               }
               else{
                   console.log("No boards found");
                   var noBoards = [{name:"--- No boards found ---"}];
                   vm.boards = noBoards;
                   vm.selectedBoard = vm.boards[0];
               }
            });
        }

        function getLogs(){
            if(vm.selectedBoard.name === "All"){
                getAllLogs();
            }
            else{
                BoardPendingActionsService.GetBoardLogs($rootScope.globals.currentUser.username,vm.selectedBoard.id).then(function(logs){
                   vm.logs = logs;
                });
            }
            vm.acceptedLogs = [];
            var l;
            for (var i = 0, len = vm.logs.length; i < len; i++) {
                l = vm.logs[i];
                console.log(l);
                if(l.accepted){
                    console.log("PUSH");
                    vm.acceptedLogs.push(l);
                }
            }
            console.log(vm.acceptedLogs);
        }

        function displayCardTracking(index){
            var log = vm.logs[index];
            vm.cardNameTracking = log.cardName;
            $('#cardTrackingModal').modal('toggle');
            BoardPendingActionsService.GetCardTracking(log.cardId).then(function(response){
                console.log(response);
                if(response.success){
                    console.log("GET CARD TRACKING SUCCESSFUL");
                    vm.cardMovements = response.trackingInfo;
                }
                else{
                    console.log("GET CARD TRACKING COMPLETED FAILURE");
                }
            });
        }

        function markAsCompleted(index){
            console.log("Mark as completed clicked!");
            console.log(vm.acceptedLogs);
            var accepted;
            if(vm.logs[index].accepted){
                vm.logs[index].accepted = false;
                accepted = false;
                for (var i = 0, len = vm.acceptedLogs.length; i < len; i++) {
                    vm.acceptedLogs.splice(i,1);
                }
            }
            else{
                vm.logs[index].accepted = true;
                accepted = true;
                vm.acceptedLogs.push(vm.logs[index]);
            }
            console.log(vm.acceptedLogs);

            BoardPendingActionsService.SetAccepted(vm.logs[index].id,accepted).then(function(response){
                console.log(response);
                if(response.success){
                    console.log("MARK AS COMPLETED SUCCESSFUL");
                }
                else{
                    console.log("MARK AS COMPLETED FAILURE");
                }
            });
        }

        function reject(index){
            vm.logToRefuseIndex = index;
            console.log("REJECT BUTTON CLICKED AND INDEX = " + index);
            $('#commentModal').modal('toggle');
        }

        function submitComment(){
            console.log("Submit clicked!");
            vm.dataLoading = true;
            //markAsCompleted(vm.logToRefuseIndex);
            var logToRefuse = vm.logs[vm.logToRefuseIndex];
            console.log(logToRefuse);
            console.log(vm.comment);
            BoardPendingActionsService.RejectCardDone($rootScope.globals.currentUser.username,logToRefuse.cardId,logToRefuse.cardName,logToRefuse.boardId,vm.comment)
                .then(function(response){
                    vm.dataLoading = false;
                    console.log(response);
                    vm.logToRefuseIndex = "";
                    getLogs();
                    if(response.success){
                        console.log("CARD REJECTION SUCCESSFUL");
                        $('#commentModal').modal('toggle');
                    }
                    else{
                        console.log("CARD REJECTION FAILURE");
                    }
                });
        }

        function replan(){
            console.log("Replan clicked!");
            console.log(vm.acceptedLogs);
            var c = confirm("Are you sure that you want to continue?");
            if (c) {
                console.log("CONFIRMED");
                vm.dataLoading = true;
                BoardPendingActionsService.Replan(vm.acceptedLogs).then(function (response) {
                    console.log(response);
                    vm.dataLoading = false;
                    if (response.success) {
                        console.log("REPLAN PETITION SUCCESSFUL");
                    }
                    else {
                        console.log("REPLAN PETITION FAILURE");
                    }
                })
            }
            else {
                vm.dataLoading = false;
            }
        }

        getBoards();

    }

})();