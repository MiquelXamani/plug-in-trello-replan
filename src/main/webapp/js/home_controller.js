(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['UserService', 'PlanService', 'TeamService', '$rootScope'];
    function HomeController(UserService, PlanService, TeamService, $rootScope) {
        var vm = this;

        vm.user = null;
        vm.teams = [];
        vm.plans = [];
        vm.unmatchedTeamMembers = [];
        vm.unmatchedPlanResources = [];

        initController();

        function initController() {
            loadPlans();
            loadTeams();
        }

        function loadPlans() {
            PlanService.GetPlans($rootScope.globals.currentUser.username)
                .then(function (plans) {
                    vm.plans = plans;
                    console.log(plans);
                    vm.selectedPlan = vm.plans[0];
                    getUnlinkedPlanResources(vm.selectedPlan);
                });
        }

        function loadTeams() {
            TeamService.GetTeams($rootScope.globals.currentUser.username)
                .then(function (teams) {
                    vm.teams = teams;
                    console.log(vm.teams);
                    //falta comprovar si es null
                    vm.selectedTeam = vm.teams[0];
                    getUnlinkedTeamMembers(vm.selectedTeam);
                });
        }

        function getUnlinkedTeamMembers(team){
            TeamService.GetUnmatchedTeamMembers($rootScope.globals.currentUser.username,team.id)
                .then(function(unmatchedTeamMembers){
                    vm.unmatchedTeamMembers = unmatchedTeamMembers;
                    console.log(vm.unmatchedTeamMembers);
                });

        }

        function getUnlinkedPlanResources(plan){
            plan.username = $rootScope.globals.currentUser.username;
            PlanService.GetUnmatchedPlanResources(plan)
                .then(function(unmatchedPlanResources){
                   vm.unmatchedPlanResources = unmatchedPlanResources;
                   console.log(unmatchedPlanResources);
                });
        }
    }

})();