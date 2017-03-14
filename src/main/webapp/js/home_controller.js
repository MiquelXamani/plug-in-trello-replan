(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['UserService', 'PlanService', 'TeamService', '$rootScope', '$q'];
    function HomeController(UserService, PlanService, TeamService, $rootScope, $q) {
        var vm = this;

        vm.user = null;
        vm.teams = [];
        vm.plans = [];
        vm.unmatchedTeamMembers = [];
        vm.unmatchedPlanResources = [];
        vm.getUnlinkedPlanResources = getUnlinkedPlanResources;
        vm.getUnlinkedTeamMembers = getUnlinkedTeamMembers;

        initController();

        function initController() {
            var promises =[PlanService.GetPlans($rootScope.globals.currentUser.username),TeamService.GetTeams($rootScope.globals.currentUser.username)];
            $q.all(promises).then(function (values){
                vm.plans = values[0];
                vm.teams = values[1];
                console.log(vm.plans);
                console.log(vm.teams);
                var allData = true;
                if(vm.plans.length > 0){
                    vm.selectedPlan = vm.plans[0];
                }
                else{
                    allData = false;
                }
                if(vm.teams.length > 0) {
                    vm.selectedTeam = vm.teams[0];
                }
                else{
                    allData = false;
                }

                if(allData){
                    var p = vm.selectedPlan;
                    p.username = $rootScope.globals.currentUser.username;
                    PlanService.GetUnmatchedPlanResources(p).then(function(unmatchedPlanResources){
                        vm.unmatchedPlanResources = unmatchedPlanResources;
                        console.log(unmatchedPlanResources);
                        if(vm.unmatchedPlanResources.length > 0){
                            TeamService.GetUnmatchedTeamMembers($rootScope.globals.currentUser.username,vm.selectedTeam.id)
                                .then(function(unmatchedTeamMembers){
                                    vm.unmatchedTeamMembers = unmatchedTeamMembers;
                                    console.log(vm.unmatchedTeamMembers);
                                });
                        }
                    });
                }
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