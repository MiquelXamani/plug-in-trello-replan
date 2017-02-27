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
            console.log("START");
            UserService.Create(vm.user)
                .then(function (response) {
                    console.log("CONTINUE");
                    if (response.success) {
                        AlertService.Success('Registration successful', true);
                        $location.path('#!/login');
                        console.log("SUCCESS");
                    } else {
                        AlertService.Error(response.message);
                        vm.dataLoading = false;
                        console.log("FAIL");
                    }
                });
        }
    }

})();