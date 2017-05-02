(function () {
    'use strict';

    angular
        .module('app')
        .controller('LogsController', LogsController);

    LogsController.$inject = ['$location','$rootScope'];
    function LogsController($location,$rootScope) {
        var vm = this;


    }

})();