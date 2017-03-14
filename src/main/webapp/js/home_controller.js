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
            loadCurrentUser();
            loadPlans(); //no implementat encara
            loadTeams();
        }

        function loadCurrentUser() {
            UserService.GetByUsername($rootScope.globals.currentUser.username)
                .then(function (user) {
                    vm.user = user;
                });
            console.log($rootScope.globals.currentUser.username);
        }

        function loadPlans() {
            //simula de forma molt bàsica el contingut de les releases
            var release1 = {
                name:"Release Març",
                resources: [
                    {
                        name:"Joan",
                        id:"1"
                    },
                    {
                        name:"Josep",
                        id:"2"
                    },
                    {
                        name:"Andreu",
                        id:"3"
                    }]
            };

            var release2 = {
                name:"Release Febrer",
                resources: [
                    {
                        name:"Maria",
                        id:"4"
                    },
                    {
                        name:"Antoni",
                        id:"5"
                    },
                    {
                        name:"Jordi",
                        id:"6"
                    },
                    {
                        name:"Cristina",
                        id:"7"
                    }]
            };

            var release3 = {
                name:"Release Gener",
                resources: [
                    {
                        name:"Carme",
                        id:"8"
                    },
                    {
                        name:"Marc",
                        id:"9"
                    },
                    {
                        name:"Edgard",
                        id:"10"
                    }]
            };

            vm.plans.push(release1,release2,release3);
            vm.selectedPlan = vm.plans[0];
            getUnlinkedPlanResources(vm.selectedPlan);

            /*
            PlanService.GetPlans($rootScope.globals.currentUser.username)
                .then(function (plans) {
                    vm.plans = plans;
                    vm.selectedPlan = vm.plans[0];
                });*/
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
            var planResources = [], pr = {}, ids = [];
            $.each(plan.resources, function(index, value){
                var found = false, i = 0;
                while(!found && i < ids.length){
                    if(value.id === ids[i]) {
                        found = true;
                    }
                    i++;
                }
                if(!found){
                    var resource = {};
                    resource.id = value.id;
                    resource.name = value.name;
                    planResources.push(resource);
                    ids.push(value.id);
                }
            });

            pr.username = $rootScope.globals.currentUser.username;
            pr.resources = planResources;
            console.log(pr);
            PlanService.GetUnmatchedPlanResources(pr)
                .then(function(unmatchedPlanResources){
                   vm.unmatchedPlanResources = unmatchedPlanResources;
                   console.log(unmatchedPlanResources);
                });
        }
    }

})();