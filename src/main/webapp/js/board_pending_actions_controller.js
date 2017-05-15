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
        }

        function displayCardTracking(index){
            $('#cardTrackingModal').modal('toggle');
        }

        function markAsCompleted(index){
            console.log("Mark as completed clicked!");
            var accepted;
            if(vm.logs[index].accepted){
                vm.logs[index].accepted = false;
                accepted = false;
            }
            else{
                vm.logs[index].accepted = true;
                accepted = true;
            }

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
            var acceptedLogs = [];
            var l;
            for (var i = 0, len = vm.logs.length; i < len; i++) {
                l = vm.logs[i];
                console.log(l);
                if(l.accepted){
                    console.log("PUSH");
                    acceptedLogs.push(l);
                }
            }
            console.log(acceptedLogs);
            var c = confirm("Are you sure that you want to continue?");
            if(c){
                console.log("CONFIRMED");
            }
            else{
                vm.dataLoading = false;
            }
        }

        getBoards();

    }

})();