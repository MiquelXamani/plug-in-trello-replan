(function () {
    'use strict';

    angular
        .module('app')
        .controller('LogsController', LogsController);

    LogsController.$inject = ['$location','$rootScope','LogService'];
    function LogsController($location,$rootScope,LogService) {
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
            LogService.GetAllLogs($rootScope.globals.currentUser.username).then(function(logs){
                vm.logs = logs;
            });
        }

        function getBoards(){
            LogService.GetBoards($rootScope.globals.currentUser.username).then(function(boards){
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
                LogService.GetBoardLogs($rootScope.globals.currentUser.username,vm.selectedBoard.id).then(function(logs){
                   vm.logs = logs;
                });
            }
        }

        function markAsRead(index){
            vm.logs[index].read = true;
            LogService.LogRead(index.id).then(function(response){
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
            $('#commentModal').modal('toggle');
        }

        function submitComment(){
            vm.dataLoading = true;
            markAsRead(vm.logToRefuseIndex);
            LogService.RejectCardDone($rootScope.globals.currentUser.username,vm.logs[logToRefuseIndex].cardId,vm.comment)
                .then(function(response){
                    vm.dataLoading = false;
                    if(response.success){
                        console.log("MARK AS READ SUCCESSFUL");
                    }
                    else{
                        console.log("MARK AS READ FAILURE");
                    }
                });
            vm.logToRefuseIndex = "";
            $('#commentModalModal').modal('toggle');
        }

        getBoards();

    }

})();