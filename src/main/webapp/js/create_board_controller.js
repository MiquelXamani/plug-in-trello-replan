(function () {
    'use strict';

    angular
        .module('app')
        .controller('BoardController', BoardController);

    BoardController.$inject = ['BoardService', '$rootScope', '$location'];
    function BoardController(BoardService, $rootScope, $location) {
        var vm = this;

        vm.dataLoading = false;
        vm.boardName;

        vm.nextStep = nextStep;
        vm.previousStep = previousStep;

        function createTrelloBoard(){
            var plan = $rootScope.plan;
            plan.username = $rootScope.globals.currentUser.username;
            plan.boardName = vm.boardName;
            plan.teamId = $rootScope.team.id;
            BoardService.LoadPlanOnTrello(plan).then(function(response){
                if(response.success) {
                    $rootScope.boardUrl = response.board.url;
                    //$location.path('/load-to-trello-completed');
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
            $location.path('/match');
        }


    }

})();