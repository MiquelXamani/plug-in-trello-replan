(function () {
    'use strict';

    angular
        .module('app', ['ngRoute', 'ngCookies'])
        .config(config)
        .run(run)
        .controller('HeaderController', ['$scope', '$location', function($scope, $location) {
            $scope.isActive = function (viewLocation) {
                if(viewLocation === $location.path()){
                    return true;
                }
                else if(viewLocation !== '/'){
                    return $location.path().indexOf(viewLocation) == 0;
                }
                else return false;

            };


        }]);

    config.$inject = ['$routeProvider', '$locationProvider'];
    function config($routeProvider, $locationProvider) {
        $routeProvider
            .when('/', {
                controller: 'BoardPendingActionsController',
                templateUrl: 'board-pending-actions.html',
                controllerAs: 'vm'
            })

            .when('/login', {
                controller: 'LoginController',
                templateUrl: 'login.html',
                controllerAs: 'vm'
            })

            .when('/register', {
                controller: 'RegisterController',
                templateUrl: 'register.html',
                controllerAs: 'vm'
            })

            .when('/load-to-trello/select-plan', {
                controller: 'SelectPlanController',
                templateUrl: 'select-plan.html',
                controllerAs: 'vm'
            })

            .when('/load-to-trello/select-team',{
                controller: 'SelectTeamController',
                templateUrl: 'select-team.html',
                controllerAs: 'vm'
            })

            .when('/load-to-trello/match',{
                controller: 'MatchController',
                templateUrl: 'matching.html',
                controllerAs: 'vm'
            })

            .when('/load-to-trello/create-board',{
                controller: 'CreateBoardController',
                templateUrl: 'create-board.html',
                controllerAs: 'vm'
            })

            .when('/load-to-trello/completed',{
                controller: 'LoadCompletedController',
                templateUrl: 'load-to-trello-completed.html',
                controllerAs: 'vm'
            })

            .when('/board-pending-actions',{
                controller: 'BoardPendingActionsController',
                templateUrl: 'board-pending-actions.html',
                controllerAs: 'vm'
            })

            .otherwise({ redirectTo: '/login' });

        $locationProvider.html5Mode(true);
        $locationProvider.hashPrefix('');
    }

    run.$inject = ['$rootScope', '$location', '$cookies', '$http'];
    function run($rootScope, $location, $cookies, $http) {
        // keep user logged in after page refresh
        $rootScope.globals = $cookies.getObject('globals') || {};
        if ($rootScope.globals.currentUser) {
            $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata;
        }

        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            // redirect to login page if not logged in and trying to access a restricted page
            var restrictedPage = $.inArray($location.path(), ['/login', '/register']) === -1;
            var loggedIn = $rootScope.globals.currentUser;
            if(restrictedPage){
                $('#bs-example-navbar-collapse-1').removeClass('visible_important');
                if(!loggedIn){
                    $location.path('/login');
                }
            }
            else{
                $('#bs-example-navbar-collapse-1').addClass('visible_important');
            }
        });
    }

})();