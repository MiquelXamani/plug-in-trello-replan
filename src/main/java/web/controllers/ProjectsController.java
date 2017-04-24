package web.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.domain.Project;
import web.domain.Release;
import web.services.ReplanService;

@RestController
@RequestMapping("/projects")
public class ProjectsController {

    @RequestMapping(method = RequestMethod.GET)
    public Project[] getProjects(){
        ReplanService replanService = new ReplanService();
        return replanService.getProjects();
    }

    @RequestMapping(value="/{projectId}/releases", method = RequestMethod.GET)
    public Release[] getReleases(@PathVariable("projectId") int projectId){
        ReplanService replanService = new ReplanService();
        return replanService.getReleases(projectId);
    }


}
