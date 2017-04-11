package web.services;

import org.springframework.web.client.RestTemplate;
import web.models.Plan;
import web.models.Project;
import web.models.Release;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplanService {
    private RestTemplate restTemplate = new RestTemplate();
    private String url;
    Map<String,String> vars;


    public ReplanService(){}

    //Function for testing purposes only
    //The final version will return Plan[]
    public List<Plan> getPlansTest(String replanUserId){
        url = "https://lit-savannah-17077.herokuapp.com/api/ui/v1/projects/{projectId}/releases/{releaseId}/plan";
        vars = new HashMap<>();
        vars.put("projectId","1");
        List<Plan> plans = new ArrayList<>();
        Plan p;
        for(int i = 1; i < 3; i++){
            vars.put("releaseId",String.valueOf(i));
            p = restTemplate.getForObject(url,Plan.class,vars);
            plans.add(p);
        }
        //Plans don't have "name" attribute, an attribute that is useful for web app
        plans.get(0).setName("March Release");
        plans.get(1).setName("February Release");
        return plans;
    }

    public Project[] getProjects(){
        url = "https://lit-savannah-17077.herokuapp.com/api/ui/v1/projects";
        return restTemplate.getForObject(url,Project[].class);
    }

    public Release[] getReleases(int projectId){
        url = "https://lit-savannah-17077.herokuapp.com/api/ui/v1/projects/" + projectId + "/releases";
        return restTemplate.getForObject(url,Release[].class);
    }
}
