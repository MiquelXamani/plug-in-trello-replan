(function () {
    'use strict';

    angular
        .module('app')
        .controller('CreateBoardController', CreateBoardController);

    CreateBoardController.$inject = ['BoardService', '$rootScope', '$location'];
    function CreateBoardController(BoardService, $rootScope, $location) {
        var vm = this;

        vm.dataLoading = false;
        vm.boardName = $rootScope.release.name;

        vm.nextStep = nextStep;
        vm.previousStep = previousStep;

        function createTrelloBoard(){
            var plan = $rootScope.plan;
            plan.username = $rootScope.globals.currentUser.username;
            plan.boardName = vm.boardName;
            plan.teamId = $rootScope.team.id;
            plan.endpointId = $rootScope.endpoint.id;
            plan.projectId = $rootScope.project.id;
            plan.releaseId = $rootScope.release.id;
            BoardService.LoadPlanOnTrello(plan).then(function(response){
                if(response.success) {
                    $rootScope.board = response.board;
                    console.log(response.board);
                    $location.path('/load-to-trello/completed');
                }
                else{
                    //show error
                }
                vm.dataLoading = false;
            });

        }

        function nextStep() {
            vm.dataLoading = true;
            if($rootScope.unmatchedResources.length > 0){
                var c = confirm("There are resources of the plan that hasn't been assigned to a Trello user. " +
                    "These resources won't be assigned to any card. " +
                    "Are you sure that you want to continue?");
                if(c){
                    console.log("CONFIRMED");
                    createTrelloBoard();
                }
                else{
                    vm.dataLoading = false;
                }
            }
            else{
                createTrelloBoard();
            }
        }




        function previousStep(){
            $location.path('/load-to-trello/match');
        }


    }

})();