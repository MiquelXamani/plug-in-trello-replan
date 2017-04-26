(function () {
    'use strict';

    angular
        .module('app')
        .controller('SelectPlanController', SelectPlanController);

    SelectPlanController.$inject = ['PlanService', '$rootScope', '$location'];
    function SelectPlanController(PlanService, $rootScope, $location) {
        var vm = this;

        vm.endpoints = [];
        vm.projects = [];
        vm.releases = [];
        vm.nextEnabled = false;

        vm.getEndpoints = getEndpoints;
        vm.getProjects = getProjects;
        vm.getReleases = getReleases;
        vm.nextStep = nextStep;

        function getReleases(){
            console.log("endpoint ID: " + vm.selectedEndpoint.id);
            PlanService.GetReleases(vm.selectedEndpoint.id,vm.selectedProject.id).then(function(releases){
                console.log(releases);
                vm.releases = releases;
                if(releases.length > 0){
                    vm.releases = releases;
                    vm.selectedRelease = vm.releases[0];
                    vm.nextEnabled = true;
                }
                else{
                    vm.nextEnabled = false;
                    var noReleases = [{name:"--- No releases found ---"}];
                    vm.releases = noReleases;
                    vm.selectedRelease = vm.releases[0];
                }
            });
        }

        function getProjects() {
            PlanService.GetProjects(vm.selectedEndpoint.id).then(function(projects){
                console.log(projects);
                if(projects.length > 0){
                    vm.projects = projects;
                    vm.selectedProject = vm.projects[0];
                    getReleases();
                }
                else{
                    var noProjects = [{name:"--- No projects found ---"}];
                    vm.projects = noProjects;
                    vm.selectedProject = vm.projects[0];
                }
            });
        }

        function getEndpoints(){
            PlanService.GetEndpoints().then(function (endpoints) {
                console.log(endpoints);
                if(endpoints.length > 0){
                    vm.endpoints = endpoints;
                    vm.selectedEndpoint = vm.endpoints[0];
                    getProjects();
                }
                else{
                    var noEndpoints = [{name:"--- No endpoints found ---"}];
                    vm.endpoints = noEndpoints;
                    vm.selectedEndpoint = vm.endpoints[0];
                }

            })
        }

        function nextStep(){
            console.log(vm.selectedEndpoint);
            console.log(vm.selectedProject);
            console.log(vm.selectedRelease);
            $rootScope.endpoint = vm.selectedEndpoint;
            $rootScope.project = vm.selectedProject;
            $rootScope.release = vm.selectedRelease;
            $location.path('/select-team');
        }

        getEndpoints();
    }

})();