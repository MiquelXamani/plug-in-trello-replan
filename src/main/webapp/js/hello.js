angular.module('hello', [])
    .controller('home', function($scope, $http) {
        $http.get('/hello-world/').then(function(data) {
            $scope.greeting = data.data;
        })
    });