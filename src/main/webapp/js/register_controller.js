(function () {
    'use strict';

    angular
        .module('app')
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['$scope','UserService', '$location', '$rootScope', 'AlertService', 'AuthenticationService'];
    function RegisterController($scope,UserService, $location, $rootScope, AlertService, AuthenticationService) {
        var vm = this;
        vm.dataLoading = false;
        vm.authorized = false;

        vm.register = register;
        vm.authorizeIntegration = authorizeIntegration;

        function register() {
            vm.dataLoading = true;
            //console.log("START");
            vm.user.trelloToken = vm.token;
            UserService.Create(vm.user)
                .then(function (response) {
                    //console.log("CONTINUE");
                    if (response.success) {
                        AuthenticationService.SetCredentials(vm.user.username, vm.user.password);
                        $location.path('/');
                        //console.log("SUCCESS");
                    } else {
                        AlertService.Error(response.message);
                        vm.dataLoading = false;
                        //console.log("FAIL");
                    }
                });
        }

        //Create popup window with Trello auth loaded.
        function authorizeIntegration(){
            //Trello API key
            var key = "504327a0a1868e4f91dae5f6c852de79";
            var authWindow, authUrl, token, trello, height, left, origin, receiveMessage, ref1, top, width;
            width = 420;
            height = 470;
            left = window.screenX + (window.innerWidth - width) / 2;
            top = window.screenY + (window.innerHeight - height) / 2;
            origin = (ref1 = /^[a-z]+:\/\/[^\/]*/.exec(location)) != null ? ref1[0] : void 0;
            //call_back=postMessage is necessary to enable cross-origin communication
            authUrl = `https://trello.com/1/authorize?return_url=${origin}&callback_method=postMessage&expiration=never&name=Project&key=${key}`
            authWindow = window.open(authUrl, 'trello', `width=${width},height=${height},left=${left},top=${top}`);
            var receiveMessage = function(event) {
                var ref2;
                if ((ref2 = event.source) != null) {
                    ref2.close();
                }
                if ((event.data != null) && /[0-9a-f]{64}/.test(event.data)) {
                    vm.token = event.data;
                    vm.authorized = true;
                    //console.log(event.data);
                } else {
                    token = null;
                    vm.token = token;
                }
                if (typeof window.removeEventListener === 'function') {
                    window.removeEventListener('message', receiveMessage, false);
                }
                $scope.$apply();
            };
            return typeof window.addEventListener === 'function' ? window.addEventListener('message', receiveMessage, false) : void 0;
        }
    }

})();