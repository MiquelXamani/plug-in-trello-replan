(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['MatchingService', 'PlanService', 'TeamService', '$rootScope', '$q'];
    function HomeController(MatchingService, PlanService, TeamService, $rootScope, $q) {
        var vm = this;

        vm.user = null;
        vm.teams = [];
        vm.plans = [];
        vm.unmatchedTeamMembers = [];
        vm.unmatchedPlanResources = [];
        vm.getUnlinkedPlanResources = getUnlinkedPlanResources;
        vm.getUnlinkedTeamMembers = getUnlinkedTeamMembers;
        vm.matchResourceMember = matchResourceMember;

        initController();

        function initController() {
            console.log("INIT CONTROLLER");
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
                        console.log("PLAN RESOURCES");
                        vm.unmatchedPlanResources = unmatchedPlanResources;
                        console.log(unmatchedPlanResources);
                        if(vm.unmatchedPlanResources.length > 0){
                            vm.selectedResource = vm.unmatchedPlanResources[0];
                            TeamService.GetUnmatchedTeamMembers($rootScope.globals.currentUser.username,vm.selectedTeam.id)
                                .then(function(unmatchedTeamMembers){
                                    vm.unmatchedTeamMembers = unmatchedTeamMembers;
                                    console.log("TEAM MEMBERS");
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
                vm.selectedResource = vm.unmatchedPlanResources[0];
                TeamService.GetUnmatchedTeamMembers($rootScope.globals.currentUser.username, teamId)
                    .then(function (unmatchedTeamMembers) {
                        vm.unmatchedTeamMembers = unmatchedTeamMembers;
                        vm.selectedTeamMember = unmatchedTeamMembers[0];
                        console.log("TEAM MEMBERS");
                        console.log(vm.unmatchedTeamMembers);
                        console.log(unmatchedTeamMembers);
                    });
            }

        }

        function getUnlinkedPlanResources(plan,teamId){
            console.log("PLAN CHANGE");
            plan.username = $rootScope.globals.currentUser.username;
            PlanService.GetUnmatchedPlanResources(plan)
                .then(function (unmatchedPlanResources) {
                    vm.unmatchedPlanResources = unmatchedPlanResources;
                    console.log("PLAN RESOURCES");
                    console.log(unmatchedPlanResources);
                    getUnlinkedTeamMembers(teamId);
                });
        }

        function matchResourceMember(resource,member){
            //console.log("MATCH CLICKED");
            var obj = {};
            obj.username = $rootScope.globals.currentUser.username;
            obj.resourceId = resource.id;
            obj.resourceName = resource.name;
            obj.trelloUserId = member.id;
            obj.trelloUsername = member.username;
            obj.trelloFullName = member.fullName;
            MatchingService.SavePlanResourceTeamMemberMatching(obj).
                then(function (association){
                    console.log("ASSOCIATION");
                    console.log(association);
                    var index = vm.unmatchedPlanResources.indexOf(vm.selectedResource);
                    vm.unmatchedPlanResources.splice(index,1);
                    vm.selectedResource = vm.unmatchedPlanResources[0];
                    index = vm.unmatchedTeamMembers.indexOf(vm.selectedTeamMember);
                    vm.unmatchedTeamMembers.splice(index,1);
                    vm.selectedTeamMember = vm.unmatchedTeamMembers[0];
                    if(vm.unmatchedPlanResources.length === 0) {
                        $('#matchingModal').modal('toggle');
                    }
            });
        }
    }

})();