angular.module('app', [ 'ngRoute', 'ngCookies' ])
    .config(function($routeProvider, $locationProvider) {

        $routeProvider.when('/', {
            templateUrl : 'home.html',
            controller : 'home',
            controllerAs : 'vm'
        }).when('/login', {
            templateUrl : 'login.html',
            controller : 'LoginController',
            controllerAs : 'vm'
        }).otherwise('/');

        $locationProvider.html5Mode(true);
        $locationProvider.hashPrefix('');

    })
    .controller('home', function($scope, $http) {
        $http.get('/hello-world/').then(function(data) {
            $scope.greeting = data.data;
        })
    })
    .controller('navigation', function() {});