package web.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import web.domain.*;
import web.persistence_controllers.PersistenceController;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReplanFake {
    private PersistenceController persistenceController;

    @Autowired
    public ReplanFake(PersistenceController persistenceController){
        this.persistenceController = persistenceController;
    }

    private String incrementDays(String date, int amount) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date startDate = dateFormat.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE,1);
        startDate = calendar.getTime();
        return dateFormat.format(startDate);
    }

    private Job delayJob(Job job, int days) throws ParseException {
        String newStartDate = incrementDays(job.getStarts(),days);
        System.out.println("Start date before: "+job.getStarts()+" Start date after: "+newStartDate);
        job.setStarts(newStartDate);
        String newEndDate = incrementDays(job.getEnds(),days);
        job.setEnds(newEndDate);
        return job;
    }

    public UpdatedPlan doReplanFake(String url, int projectId, int releaseId, JobsToReplan jobsToReplan){
        List<CompletedJob> completedJobs = jobsToReplan.getCompletedJobs();
        List<InProgressJob> inProgressJobs = jobsToReplan.getInProgressJobs();
        UpdatedPlan updatedPlan = new UpdatedPlan();
        if(projectId == 4){
            switch (releaseId){
                case 6:
                    //Disseny BD feature suffers a delay of 1 day, 3 features out
                    //Gestió inventari, Pàgina info usuari and Pàgina producte
                    List<Integer> jobsOut = new ArrayList<>();
                    jobsOut.add(21);
                    jobsOut.add(22);
                    jobsOut.add(20);
                    updatedPlan.setJobs_out(jobsOut);
                    List<Integer> jobsNoModify = new ArrayList<>();
                    for (CompletedJob cj:completedJobs) {
                        jobsNoModify.add(cj.getJob_id());
                    }
                    for (InProgressJob ipj:inProgressJobs) {
                        jobsNoModify.add(ipj.getJob_id());
                    }
                    List<Job> replannedJobs = persistenceController.getChangeableJobs(releaseId,jobsNoModify);
                    //Delay all jobs one day (startdate and endate)
                    List<Job> jobsDelayed = new ArrayList<>();
                    Job aux;
                    for (Job j:replannedJobs) {
                        try {
                            aux = delayJob(j,1);
                            jobsDelayed.add(aux);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    updatedPlan.setJobs(jobsDelayed);
                    //updatedPlan.setCreated_at();
                    updatedPlan.setId(releaseId);
                    break;
                default:
                    break;
            }
        }
        return updatedPlan;
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
        if(Integer.parseInt(projectId) == 4){
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
