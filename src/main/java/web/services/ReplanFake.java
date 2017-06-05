package web.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import web.domain.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ReplanFake {
    public ReplanFake(){}

    public UpdatedPlan doReplanFake(String url, int projectId, int releaseId, JobsToReplan jobsToReplan){

        return new UpdatedPlan();
    }

    public Project[] getProjects(){
        Project project = new Project(4,"E-commerce llibres");
        Project[] projects = new Project[1];
        projects[0] = project;
        return  projects;
    }

    public Release[] getReleases(int projectId){
        if(projectId == 4) {
            Release release = new Release(5, "June Release");
            Release[] releases = new Release[1];
            releases[0] = release;
            return releases;
        }
        else{
            System.out.println("ProjectId incorrect");
            return new Release[0];
        }
    }

    public Plan getPlan(String projectId, String releaseId){
        Plan plan = new Plan();
        if(Integer.parseInt(projectId) == 4 && Integer.parseInt(releaseId) == 5){
            //Load json file as a plan
            ObjectMapper mapper = new ObjectMapper();
            try {
                plan = mapper.readValue(new File("src/main/java/web/plan_examples/plan_ecommerce_llibres.json"),Plan.class);
                System.out.println("Plan size jackson: "+plan.getJobs().size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("ProjectId or releaseId incorrect");
        }
        return plan;
    }
}
