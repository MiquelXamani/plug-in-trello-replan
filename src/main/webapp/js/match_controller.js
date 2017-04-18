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
        vm.selectedMatchingIndex = null;
        vm.selectedResourceIndex = null;
        vm.selectedMemberIndex = null;
        vm.dataLoading = false;
        vm.newMatchings = [];
        vm.delMatchings = [];

        vm.setSelectedMatching = setSelectedMatching;
        vm.setSelectedResource = setSelectedResource;
        vm.setSelectedMember = setSelectedMember;
        vm.match = match;
        vm.nextStep = nextStep;
        vm.previousStep = previousStep;

        function getMatchings(){
            MatchingService.GetMatchings($rootScope.globals.currentUser.username,$rootScope.project.id,$rootScope.release.id,$rootScope.team.id)
                .then(function(response){
                    vm.matchings = response.matchings;
                    vm.unmatchedResources = response.unmatchedResources;
                    vm.unmatchedMembers = response.unmatchedMembers;
                    vm.plan = response.plan;
                    console.log("Plan:");
                    console.log(vm.plan);
                    console.log("Matchings:");
                    console.log(vm.matchings);
                    console.log("Unmatched Resources:");
                    console.log(vm.unmatchedResources);
                    console.log("Unmatched Team Members:");
                    console.log(vm.unmatchedMembers);
            });
        }

        function setSelectedMatching(index){
            console.log("INDEX:");
            console.log(index);
            console.log(vm.selectedMatchingIndex);
            if(vm.selectedMatchingIndex === index){
                vm.selectedMatchingIndex = null;
            }
            else{
                vm.selectedMatchingIndex = index;
            }
            console.log("MATCHING CLICKED!!");
        }

        function deleteMatching(){
            console.log("DELETE CLICKED");
            var resource = vm.matchings[vm.selectedMatchingIndex].resource;
            var member = vm.matchings[vm.selectedMatchingIndex].member;
            vm.delMatchings.push(vm.matchings[vm.selectedMatchingIndex]);
            vm.matchings.splice(vm.selectedMatching,1);
            vm.selectedMatchingIndex = null;
            vm.unmatchedResources.push(resource);
            vm.unmatchedMembers.push(member);
        }

        function setSelectedResource(index){
            if(vm.selectedResourceIndex === index){
                vm.selectedResourceIndex = null;
            }
            else{
                vm.selectedResourceIndex = index;
            }
            console.log("RESOURCE CLICKED!!!");

        }

        function setSelectedMember(index){
            if(vm.selectedMemberIndex === index){
                vm.selectedMemberIndex = null;
            }
            else {
                vm.selectedMemberIndex = index;
            }
            console.log("MEMBER CLICKED!!");
        }

        function match(){
            console.log("MATCH CLICKED!!!")
            var matching = {};
            matching.resource = vm.unmatchedResources[vm.selectedResourceIndex];
            matching.member = vm.unmatchedMembers[vm.selectedMemberIndex];
            vm.matchings.push(matching);
            vm.unmatchedResources.splice(vm.selectedResourceIndex,1);
            vm.unmatchedMembers.splice(vm.selectedMemberIndex,1);
            vm.selectedResourceIndex = null;
            vm.selectedMemberIndex = null;
            vm.newMatchings.push(matching);
        }

        function nextStep(){
            console.log("NEW MATCHINGS");
            console.log(vm.newMatchings);
            vm.dataLoading = true;
            if(vm.newMatchings.length > 0) {
                MatchingService.SavePlanResourceTeamMemberMatchings($rootScope.globals.currentUser.username,vm.newMatchings)
                    .then(function(response){
                        console.log("MATCHINGS CREATION SUCCESSFUL");
                        if(vm.delMatchings.length > 0) {
                            MatchingService.DeletePlanResourceTeamMemberMatchings($rootScope.globals.currentUser.username, vm.delMatchings)
                                .then(function (response) {
                                    console.log("MATCHINGS DELETION SUCCESSFUL");
                                    vm.dataLoading = false;
                           });
                   }
                   else{
                       vm.dataLoading = false;
                   }
                });
            }
            else{
                vm.dataLoading = false;
            }
            //$location.path('/create-board');
        }

        function previousStep(){
            $location.path('/select-team');
        }

        getMatchings();

    }

})();