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
                resources: ["Joan","Josep","Andreu"]
            };

            var release2 = {
                name:"Release Febrer",
                resources: ["Maria","Antoni","Jordi","Cristina"]
            };

            var release3 = {
                name:"Release Gener",
                resources: ["Carme","Marc","Edgard"]
            };

            vm.plans.push(release1,release2,release3);
            vm.selectedPlan = vm.plans[0];

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
                });
        }
    }

})();