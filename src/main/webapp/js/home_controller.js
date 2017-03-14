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
                            vm.selectedResource = vm.unmatchedPlanResources[0];
                            TeamService.GetUnmatchedTeamMembers($rootScope.globals.currentUser.username,vm.selectedTeam.id)
                                .then(function(unmatchedTeamMembers){
                                    vm.unmatchedTeamMembers = unmatchedTeamMembers;
                                    console.log(vm.unmatchedTeamMembers);
                                    if(vm.unmatchedTeamMembers.length > 0){
                                        vm.selectedTeamMember = vm.unmatchedTeamMembers[0];
                                    }
                                });
                        }
                    });
                }
            });
        }

        function getUnlinkedTeamMembers(teamId){
            console.log("TEAM CHANGE");
            if(vm.unmatchedPlanResources.length > 0) {
                TeamService.GetUnmatchedTeamMembers($rootScope.globals.currentUser.username, teamId)
                    .then(function (unmatchedTeamMembers) {
                        vm.unmatchedTeamMembers = unmatchedTeamMembers;
                        console.log(vm.unmatchedTeamMembers);
                    });
            }

        }

        function getUnlinkedPlanResources(plan,teamId){
            console.log("PLAN CHANGE");
            plan.username = $rootScope.globals.currentUser.username;
            PlanService.GetUnmatchedPlanResources(plan)
                .then(function (unmatchedPlanResources) {
                    vm.unmatchedPlanResources = unmatchedPlanResources;
                    console.log(unmatchedPlanResources);
                    getUnlinkedTeamMembers(teamId);
                });
        }
    }

})();