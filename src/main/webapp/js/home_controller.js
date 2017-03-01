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
            /*
            PlanService.GetPlans($rootScope.globals.currentUser.username)
                .then(function (plans) {
                    vm.plans = plans;
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