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
            Release genericRelease = new Release(5, "June Release Generic");
            Release featureOut = new Release(6, "\"Pàg home\" s'allarga fent que \"Pàg info usuari\" quedi fora");
            Release featureIn = new Release(7, "\"Pàg producte\" acaba abans d'hora i s'afegeix nova feature a release");
            Release canviDates = new Release(8, "\"Cercar producte\" acaba abans d'hora i es canvien les dates de cards següents");
            Release canviRecursAssignat = new Release(9, "Sergi acaba abans d'hora \"Maq. parts comunes\" i se li assigna \"FAQ\" que era de la Maria");
            Release canviDeNextCard = new Release(10, "Albert acaba \"Login\" i la seva next card era \"Obt info usuari\" però passa a ser \"Control sessions\"");
            Release[] releases = new Release[6];
            releases[0] = genericRelease;
            releases[1] = featureOut;
            releases[2] = featureIn;
            releases[3] = canviDates;
            releases[4] = canviRecursAssignat;
            releases[5] = canviDeNextCard;
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
