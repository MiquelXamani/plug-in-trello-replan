(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['MatchingService', 'PlanService', 'TeamService', '$rootScope', '$q', 'BoardService'];
    function HomeController(MatchingService, PlanService, TeamService, $rootScope, $q, BoardService) {
        var vm = this;

        vm.user = null;
        vm.teams = [];
        vm.plans = [];
        vm.dataLoading = false;
        vm.unmatchedTeamMembers = [];
        vm.unmatchedPlanResources = [];
        vm.getUnlinkedPlanResources = getUnlinkedPlanResources;
        vm.getUnlinkedTeamMembers = getUnlinkedTeamMembers;
        vm.matchResourceMember = matchResourceMember;
        vm.loadToTrello = loadToTrello;

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

        function getUnlinkedTeamMembers(){
            console.log("TEAM CHANGE");
            if(vm.unmatchedPlanResources.length > 0) {
                vm.selectedResource = vm.unmatchedPlanResources[0];
                TeamService.GetUnmatchedTeamMembers($rootScope.globals.currentUser.username, vm.selectedTeam.id)
                    .then(function (unmatchedTeamMembers) {
                        vm.unmatchedTeamMembers = unmatchedTeamMembers;
                        vm.selectedTeamMember = unmatchedTeamMembers[0];
                        console.log("TEAM MEMBERS");
                        console.log(vm.unmatchedTeamMembers);
                        console.log(unmatchedTeamMembers);
                    });
            }

        }

        function getUnlinkedPlanResources(){
            console.log("PLAN CHANGE");
            vm.selectedPlan.username = $rootScope.globals.currentUser.username;
            PlanService.GetUnmatchedPlanResources(vm.selectedPlan)
                .then(function (unmatchedPlanResources) {
                    vm.unmatchedPlanResources = unmatchedPlanResources;
                    console.log("PLAN RESOURCES");
                    console.log(unmatchedPlanResources);
                    getUnlinkedTeamMembers();
                });
        }

        function matchResourceMember(){
            //console.log("MATCH CLICKED");
            var obj = {};
            obj.username = $rootScope.globals.currentUser.username;
            obj.resourceId = vm.selectedResource.id;
            obj.resourceName = vm.selectedResource.name;
            obj.trelloUserId = vm.selectedTeamMember.id;
            obj.trelloUsername = vm.selectedTeamMember.username;
            obj.trelloFullName = vm.selectedTeamMember.fullName;
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

        function loadToTrello(){
            vm.dataLoading = true;
            if(vm.unmatchedPlanResources.length > 0){
                var c = confirm("There are resources of the plan that hasn't been assigned to a Trello user. " +
                    "These resources won't be assigned to any card. " +
                    "Are you sure that you want to continue?");
                if(c){
                    console.log("CONFIRMED");
                    vm.selectedPlan.boardName = vm.boardName;
                    vm.selectedPlan.teamId = vm.selectedTeam.id;
                    BoardService.LoadPlanOnTrello(vm.selectedPlan).
                        then(function(board){
                            console.log(board);
                            vm.dataLoading = false;
                            //redireccionar a gestionar taulers
                    });
                }
                else{
                    vm.dataLoading = false;
                }
            }
        }
    }

})();