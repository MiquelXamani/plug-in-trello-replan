(function () {
    'use strict';

    angular
        .module('app')
        .controller('MatchController', MatchController);

    MatchController.$inject = ['MatchingService', '$rootScope', '$location'];
    function MatchController(MatchingService, $rootScope, $location) {
        var vm = this;

        vm.matchings = [];
        vm.unmatchedResources = [];
        vm.unmatchedMembers = [];
        vm.plan = {};

        vm.nextStep = nextStep;
        vm.previousStep = previousStep;

        function getMatchings(){
            MatchingService.GetMatchings($rootScope.globals.currentUser.username,$rootScope.project.id,$rootScope.release.id,$rootScope.team.id)
                .then(function(response){
                    vm.matchings = response.matchings;
                    vm.unmatchedResources = response.unmatchedResources;
                    vm.unmatchedMembers = response.unmatchedMembers;
                    vm.plan = response.plan;
            });
        }

        function nextStep(){
            console.log(vm.selectedTeam);
            $rootScope.team = vm.selectedTeam;
            //$location.path('/create-board');
        }

        function previousStep(){
            $location.path('/select-team');
        }

        getMatchings();

    }

})();