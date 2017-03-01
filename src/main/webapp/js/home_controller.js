(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['UserService', '$rootScope'];
    function HomeController(UserService, $rootScope) {
        var vm = this;

        vm.user = null;
        vm.teams = [];
        vm.plans = [];

        initController();

        function initController() {
            loadCurrentUser();
            loadPlans();
            loadTeams();
        }

        function loadCurrentUser() {
            UserService.GetByUsername($rootScope.globals.currentUser.username)
                .then(function (user) {
                    vm.user = user;
                });
        }

        function loadPlans() {
            UserService.GetAll()
                .then(function (users) {
                    vm.allUsers = users;
                });
        }

        function loadTeams() {
            UserService.GetAll()
                .then(function (users) {
                    vm.allUsers = users;
                });
        }
    }

})();