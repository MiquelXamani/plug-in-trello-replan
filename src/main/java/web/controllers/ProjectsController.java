package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.domain.Endpoint;
import web.domain.Project;
import web.domain.Release;
import web.persistence_controllers.PersistenceController;
import web.services.ReplanFake;
import web.services.ReplanService;

import java.util.List;

@RestController
@RequestMapping("/replan-endpoints")
public class ProjectsController {
    private PersistenceController persistenceController;

    @Autowired
    public ProjectsController(PersistenceController persistenceController){
        this.persistenceController = persistenceController;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Endpoint> getEndpoints(){
        return persistenceController.getEndpoints();
    }

    @RequestMapping(value="/{endpointId}/projects", method = RequestMethod.GET)
    public Project[] getProjects(@PathVariable("endpointId") int endpointId){
        String url = persistenceController.getEndpoint(endpointId).getUrl();
        if(url.equals("simulation mode")){
            ReplanFake replanFake = new ReplanFake();
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
            ReplanFake replanFake = new ReplanFake();
            return replanFake.getReleases(projectId);
        }
        else {
            ReplanService replanService = new ReplanService();
            System.out.println("Controller endpointId = " + endpointId);
            String url = persistenceController.getEndpoint(endpointId).getUrl();
            return replanService.getReleases(url, projectId);
        }
    }


}
