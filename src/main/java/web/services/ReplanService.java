package web.services;

import org.springframework.web.client.RestTemplate;
import web.domain.Plan;
import web.domain.Project;
import web.domain.Release;

import java.util.HashMap;
import java.util.Map;

public class ReplanService {
    private RestTemplate restTemplate = new RestTemplate();
    private String url;
    Map<String,String> vars;


    public ReplanService(){}

    public Plan getPlan(String url, String projectId, String releaseId){
        //url = "https://lit-savannah-17077.herokuapp.com/api/ui/v1/projects/{projectId}/releases/{releaseId}/plan";
        url += "/projects/{projectId}/releases/{releaseId}/plan";
        vars = new HashMap<>();
        vars.put("projectId",projectId);
        vars.put("releaseId",releaseId);
        return restTemplate.getForObject(url,Plan.class,vars);
    }

    public Project[] getProjects(String url){
        //url = "https://lit-savannah-17077.herokuapp.com/api/ui/v1/projects";
        url += "/projects";
        return restTemplate.getForObject(url,Project[].class);
    }

    public Release[] getReleases(String url, int projectId){
        //url = "https://lit-savannah-17077.herokuapp.com/api/ui/v1/projects/" + projectId + "/releases";
        url += "/projects/" + projectId + "/releases";
        return restTemplate.getForObject(url,Release[].class);
    }
}
