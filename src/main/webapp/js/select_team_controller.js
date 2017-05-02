(function () {
    'use strict';

    angular
        .module('app')
        .controller('SelectTeamController', SelectTeamController);

    SelectTeamController.$inject = ['TeamService', '$rootScope', '$location'];
    function SelectTeamController(TeamService, $rootScope, $location) {
        var vm = this;

        vm.teams = [];
        vm.teamMembers = [];
        vm.nextEnabled = false;

        vm.getTeamMembers = getTeamMembers;
        vm.nextStep = nextStep;
        vm.previousStep = previousStep;

        function getTeams(){
            TeamService.GetTeams($rootScope.globals.currentUser.username).then(function(teams){
                console.log(teams);
                vm.teams = teams;
                if(vm.teams.length > 0){
                    vm.selectedTeam = vm.teams[0];
                    vm.nextEnabled = true;
                    getTeamMembers();
                }
                else{
                    var noTeams = [{name:"You don't belong to any Trello team"}];
                    vm.selectedTeam = noTeams;
                }
            });
        }

        function getTeamMembers(){
            TeamService.GetTeamMembers($rootScope.globals.currentUser.username,vm.selectedTeam.id).then(function(members){
                console.log(members);
                vm.teamMembers = members;
            });
        }

        function nextStep(){
            console.log(vm.selectedTeam);
            $rootScope.team = vm.selectedTeam;
            $location.path('/load-to-trello/match');
        }

        function previousStep(){
            $location.path('/load-to-trello/select-plan');
        }

        getTeams();
    }

})();