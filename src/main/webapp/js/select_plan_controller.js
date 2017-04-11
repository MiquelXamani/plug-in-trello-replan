(function () {
    'use strict';

    angular
        .module('app')
        .controller('SelectPlanController', SelectPlanController);

    SelectPlanController.$inject = ['PlanService', '$rootScope', '$location'];
    function SelectPlanController(PlanService, $rootScope, $location) {
        var vm = this;

        vm.projects = [];
        vm.releases = [];
        vm.nextEnabled = false;

        vm.getProjects = getProjects;
        vm.nextStep = nextStep;

        function getReleases(){
            PlanService.GetReleases(vm.selectedProject.id).then(function(releases){
                console.log(releases);
                vm.releases = releases;
                if(vm.releases.length > 0){
                    vm.selectedRelease = vm.releases[0];
                    vm.nextEnabled = true;
                }
                else{
                    var noReleases = [{name:"No releases of the project selected were found"}];
                    vm.selectedRelease = noReleases;
                }
            });
        }

        function getProjects() {
            PlanService.GetProjects().then(function(projects){
                console.log(projects);
                vm.projects = projects;
                if(vm.projects.length > 0){
                    vm.selectedProject = vm.projects[0];
                    getReleases();
                }
                else{
                    var noProjects = [{name:"No projects found"}];
                    vm.selectedProject = noProjects;
                }
            });
        }

        function nextStep(){
            console.log(vm.selectedProject);
            console.log(vm.selectedRelease);
            $rootScope.project = vm.selectedProject;
            $rootScope.release = vm.selectedRelease;
            $location.path('/select-team');
        }

        getProjects();
    }

})();