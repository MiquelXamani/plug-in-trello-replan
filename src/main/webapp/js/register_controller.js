(function () {
    'use strict';

    angular
        .module('app')
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['UserService', '$location', '$rootScope', 'AlertService'];
    function RegisterController(UserService, $location, $rootScope, AlertService) {
        var vm = this;

        vm.register = register;

        function register() {
            vm.dataLoading = true;
            UserService.Create(vm.user)
                .then(function (response) {
                    if (response.success) {
                        AlertService.Success('Registration successful', true);
                        $location.path('/login');
                    } else {
                        AlertService.Error(response.message);
                        vm.dataLoading = false;
                    }
                });
        }
    }

})();