package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.dtos.Endpoint;
import web.dtos.Project;
import web.dtos.Release;
import web.domain_controllers.DomainController;
import web.services.ReplanFake;
import web.services.ReplanService;

import java.util.List;

@RestController
@RequestMapping("/replan-endpoints")
public class ProjectsController {
    private DomainController domainController;

    @Autowired
    public ProjectsController(DomainController domainController){
        this.domainController = domainController;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Endpoint> getEndpoints(){
        return domainController.getEndpoints();
    }

    @RequestMapping(value="/{endpointId}/projects", method = RequestMethod.GET)
    public Project[] getProjects(@PathVariable("endpointId") int endpointId){
        String url = domainController.getEndpoint(endpointId).getUrl();
        if(url.equals("simulation mode")){
            ReplanFake replanFake = new ReplanFake(domainController);
            return replanFake.getProjects();
        }
        else {
            ReplanService replanService = new ReplanService();
            return replanService.getProjects(url);
        }
    }

    @RequestMapping(value="/{endpointId}/projects/{projectId}/releases", method = RequestMethod.GET)
    public Release[] getReleases(@PathVariable("endpointId") int endpointId,@PathVariable("projectId") int projectId){
        if(endpointId == 2){
            ReplanFake replanFake = new ReplanFake(domainController);
            return replanFake.getReleases(projectId);
        }
        else {
            ReplanService replanService = new ReplanService();
            System.out.println("Controller endpointId = " + endpointId);
            String url = domainController.getEndpoint(endpointId).getUrl();
            return replanService.getReleases(url, projectId);
        }
    }


}
