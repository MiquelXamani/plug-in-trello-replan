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
        vm.markAsRead = markAsRead;
        vm.reject = reject;
        vm.submitComment = submitComment;

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

        function markAsRead(index){
            vm.logs[index].read = true;
            BoardPendingActionsService.LogRead(index.id).then(function(response){
                if(response.success){
                    console.log("MARK AS READ SUCCESSFUL");
                }
                else{
                    console.log("MARK AS READ FAILURE");
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
            //markAsRead(vm.logToRefuseIndex);
            var logToRefuse = vm.logs[vm.logToRefuseIndex];
            console.log(logToRefuse);
            console.log(vm.comment);
            BoardPendingActionsService.RejectCardDone($rootScope.globals.currentUser.username,logToRefuse.cardId,logToRefuse.cardName,logToRefuse.boardId,vm.comment)
                .then(function(response){
                    vm.dataLoading = false;
                    console.log(response);
                    vm.logToRefuseIndex = "";
                    $('#commentModalModal').modal('toggle');
                    if(response.success){
                        console.log("CARD REJECTION SUCCESSFUL");
                    }
                    else{
                        console.log("CARD REJECTION FAILURE");
                    }
                });
        }

        getBoards();

    }

})();