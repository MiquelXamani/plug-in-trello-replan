(function () {
    'use strict';

    angular
        .module('app')
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['UserService', '$location', '$rootScope', 'AlertService'];
    function RegisterController(UserService, $location, $rootScope, AlertService) {
        var vm = this;

        vm.register = register;
        vm.trelloAuth = false;
        vm.authorizeIntegration = authorizeIntegration;

        function register() {
            vm.dataLoading = true;
            //console.log("START");
            UserService.Create(vm.user)
                .then(function (response) {
                    //console.log("CONTINUE");
                    if (response.success) {
                        AlertService.Success('Registration successful', true);
                        $location.path('/login');
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
                    //guardar el token a localstorage per recuperar-lo al moment de fer submit i borrar-lo després de localstorage
                    console.log(event.data);
                } else {
                    token = null;
                }
                if (typeof window.removeEventListener === 'function') {
                    window.removeEventListener('message', receiveMessage, false);
                }
            };
            return typeof window.addEventListener === 'function' ? window.addEventListener('message', receiveMessage, false) : void 0;
        }
    }

})();