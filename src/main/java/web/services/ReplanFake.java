package web.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import web.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import web.domain_controllers.DomainController;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReplanFake {
    private DomainController domainController;

    @Autowired
    public ReplanFake(DomainController domainController){
        this.domainController = domainController;
    }

    private String incrementDays(String date, int amount) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date startDate = dateFormat.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE,amount);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println("Day of month: "+dayOfMonth);
        int amm;
        if(dayOfMonth == 24 || dayOfMonth == 1){
            if(amount < 0){
                amm = -1;
            }
            else{
                amm = 2;
            }
            calendar.add(Calendar.DATE,amm);
        }
        else if(dayOfMonth == 25 ||dayOfMonth == 2){
            if(amount < 0){
                amm = -2;
            }
            else{
                amm = 1;
            }
            calendar.add(Calendar.DATE,amm);
        }
        startDate = calendar.getTime();
        return dateFormat.format(startDate);
    }

    private Job delayJob(Job job, int days) throws ParseException {
        String newStartDate = incrementDays(job.getStarts(),days);
        //System.out.println("Start date before: "+job.getStarts()+" Start date after: "+newStartDate);
        job.setStarts(newStartDate);
        String newEndDate = incrementDays(job.getEnds(),days);
        //System.out.println("End date before: "+job.getEnds()+" End date after: "+newEndDate);
        job.setEnds(newEndDate);
        return job;
    }

    public UpdatedPlan doReplanFake(String url, int projectId, int releaseId, JobsToReplan jobsToReplan){
        List<CompletedJob> completedJobs = jobsToReplan.getCompletedJobs();
        List<InProgressJob> inProgressJobs = jobsToReplan.getInProgressJobs();
        UpdatedPlan updatedPlan = new UpdatedPlan();
        List<Integer> jobsNoModify = new ArrayList<>();
        List<Job> replannedJobs;
        Resource resource;
        if(projectId == 4 || projectId == 5){
            String printjobsids="";
            for (CompletedJob cj:completedJobs) {
                jobsNoModify.add(cj.getJob_id());
                printjobsids += " "+cj.getJob_id();
            }
            for (InProgressJob ipj:inProgressJobs) {
                jobsNoModify.add(ipj.getJob_id());
                printjobsids+=" "+ipj.getJob_id();
            }
            System.out.println(printjobsids);
            List<Integer> jobsOut;
            List<Job> jobsDelayed, jobsAdvanced;
            Feature newFeature;
            Job dependingJob, aux, extraJob;
            List<JobReduced> prevs;
            replannedJobs = domainController.getChangeableJobs(releaseId,jobsNoModify);
            switch (releaseId){
                case 6:
                    //Disseny BD feature suffers a delay of 1 day, 3 features out
                    //Gestió inventari, Pàgina info usuari and Pàgina producte
                    jobsOut = new ArrayList<>();
                    jobsOut.add(21);
                    jobsOut.add(22);
                    jobsOut.add(20);
                    updatedPlan.setJobs_out(jobsOut);
                    //Delay all jobs one day (startdate and endate)
                    jobsDelayed = new ArrayList<>();
                    for (Job j:replannedJobs) {
                        try {
                            aux = delayJob(j,1);
                            jobsDelayed.add(aux);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Jobs delayed size:"+jobsDelayed.size());
                    updatedPlan.setJobs(jobsDelayed);
                    //updatedPlan.setCreated_at();
                    updatedPlan.setId(releaseId);
                    break;
                case 7:
                    newFeature = new Feature(34,"Banner publicitari","Bla, bla, bla",16.0,"2017-06-23T17:00:00.000Z");
                    resource = domainController.getResourceByName("Sergi");
                    dependingJob = domainController.getJobByJobId(19,releaseId);
                    prevs = new ArrayList<>();
                    prevs.add(new JobReduced(dependingJob.getId(),dependingJob.getStarts(),dependingJob.getEnds(),dependingJob.getFeature().getId(),dependingJob.getResource().getId()));
                    extraJob = new Job(23,"2017-06-22T09:00:00.000Z",resource,newFeature,prevs,"2017-06-23T17:00:00.000Z");

                    replannedJobs.add(extraJob);
                    updatedPlan.setJobs(replannedJobs);
                    updatedPlan.setId(releaseId);
                    break;
                case 9:
                    //Sergi finished Maq parts comunes earlier than expected while Maria is working on Pàgina contacte
                    //Replan assigns to Sergi the feature FAQ, that was previously assigned to Maria
                    //Find the job that contains FAQ and replace resource Maria by Sergi
                    for(int i = 0; i < replannedJobs.size(); i++){
                        if(replannedJobs.get(i).getFeature().getName().equals("FAQ")){
                            resource = domainController.getResourceByName("Sergi");
                            replannedJobs.get(i).setResource(resource);
                            replannedJobs.get(i).setStarts("2017-06-05T11:00:00.000Z");
                            replannedJobs.get(i).setEnds("2017-06-05T16:00:00.000Z");
                        }
                    }
                    updatedPlan.setJobs(replannedJobs);
                    updatedPlan.setId(releaseId);
                    break;
                case 10:
                    //When Albert finished Login, Obtenir informació usuari was expected to be his next card
                    //but a replanification took place and Control sessions was assigned as his new next card
                    //because of that, Control sessions and Obtenir informació usuari changes their start date and due date
                    int controlSessionsIndex = -1, obtInfoUserIndex = -1;
                    String controlSessionsStarts = "", controlSessionsEnds = "", obtInfoUserStarts = "", obtInfoUserEnds = "";
                    for(int i = 0; i < replannedJobs.size(); i++){
                        if(replannedJobs.get(i).getFeature().getName().equals("Control sessions")){
                            controlSessionsIndex = i;
                            controlSessionsStarts = replannedJobs.get(i).getStarts();
                            controlSessionsEnds = replannedJobs.get(i).getEnds();
                        }
                        else if(replannedJobs.get(i).getFeature().getName().equals("Obtenir informació usuari")){
                            obtInfoUserIndex = i;
                            obtInfoUserStarts = replannedJobs.get(i).getStarts();
                            obtInfoUserEnds = replannedJobs.get(i).getEnds();
                        }
                    }

                    Job controlSessionsJob = replannedJobs.get(controlSessionsIndex);
                    controlSessionsJob.setStarts(obtInfoUserStarts);
                    controlSessionsJob.setEnds(obtInfoUserEnds);
                    replannedJobs.set(controlSessionsIndex,controlSessionsJob);

                    Job obtInfoUserJob = replannedJobs.get(obtInfoUserIndex);
                    obtInfoUserJob.setStarts(controlSessionsStarts);
                    obtInfoUserJob.setEnds(controlSessionsEnds);
                    replannedJobs.set(obtInfoUserIndex,obtInfoUserJob);

                    updatedPlan.setJobs(replannedJobs);
                    updatedPlan.setId(releaseId);
                    break;
                case 11:
                    //"Cerca de producte" feature suffers a delay of 1 day, 2 features out
                    //"Recuperació de contrasenya" and "Pàgina producte"
                    jobsOut = new ArrayList<>();
                    jobsOut.add(7);
                    jobsOut.add(12);
                    updatedPlan.setJobs_out(jobsOut);
                    //Delay all jobs one day (startdate and endate)
                    jobsDelayed = new ArrayList<>();
                    for (Job j:replannedJobs) {
                        try {
                            if(j.getId() == 11) {
                                //Back-end admin takes the place of pàgina del producte
                                j.setStarts("2017-07-03T09:00:00.000Z");
                                j.setEnds("2017-07-04T17:00:00.000Z");
                                aux = j;
                            }
                            else{
                                aux = delayJob(j,1);
                            }
                            jobsDelayed.add(aux);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Jobs delayed size:"+jobsDelayed.size());
                    updatedPlan.setJobs(jobsDelayed);
                    updatedPlan.setId(releaseId);
                    break;
                case 12:
                    newFeature = new Feature(13,"Pàgina informació de l'usuari","Bla, bla, bla",1.0,"2017-07-07T17:00:00.000Z");
                    resource = domainController.getResourceByName("Josep");
                    dependingJob = domainController.getJobByJobId(10,releaseId);
                    prevs = new ArrayList<>();
                    prevs.add(new JobReduced(dependingJob.getId(),dependingJob.getStarts(),dependingJob.getEnds(),dependingJob.getFeature().getId(),dependingJob.getResource().getId()));
                    dependingJob = domainController.getJobByJobId(12,releaseId);
                    prevs.add(new JobReduced(dependingJob.getId(),dependingJob.getStarts(),dependingJob.getEnds(),dependingJob.getFeature().getId(),dependingJob.getResource().getId()));
                    extraJob = new Job(13,"2017-07-06T09:00:00.000Z",resource,newFeature,prevs,"2017-07-06T17:00:00.000Z");

                    jobsAdvanced = new ArrayList<>();
                    for (Job j:replannedJobs) {
                        try {
                            aux = delayJob(j,-1);
                            jobsAdvanced.add(aux);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    jobsAdvanced.add(extraJob);
                    updatedPlan.setJobs(jobsAdvanced);
                    updatedPlan.setId(releaseId);
                    break;
                default:
                    break;
            }
        }
        return updatedPlan;
    }

    public Project[] getProjects(){
        Project project = new Project(4,"E-commerce llibres TESTING");
        Project[] projects = new Project[2];
        projects[0] = project;
        projects[1] = new Project(5,"E-commerce llibres DEMO");
        return  projects;
    }

    public Release[] getReleases(int projectId){
        Release[] releases;
        if(projectId == 4) {
            Release genericRelease = new Release(5, "June Release Generic");
            Release featureOut = new Release(6, "\"Disseny Bd\" s'allarga fent que \"Pàg info usuari\", \"Gestió inventari\" i \"Pàgina producte\" quedin fora");
            Release featureIn = new Release(7, "\"Pàg producte\" acaba abans d'hora i s'afegeix nova feature a release");
            Release canviDates = new Release(8, "\"Cercar producte\" acaba abans d'hora i es canvien les dates de cards següents");
            Release canviRecursAssignat = new Release(9, "Sergi acaba abans d'hora \"Maq. parts comunes\" i se li assigna \"FAQ\" que era de la Maria");
            Release canviDeNextCard = new Release(10, "Albert acaba \"Login\" i la seva next card era \"Control sessions\" però passa a ser \"Obt info usuari\"");
            releases = new Release[6];
            releases[0] = genericRelease;
            releases[1] = featureOut;
            releases[2] = featureIn;
            releases[3] = canviDates;
            releases[4] = canviRecursAssignat;
            releases[5] = canviDeNextCard;
        }
        else if(projectId == 5){
            Release featuresOutDemo = new Release(11,"Eliminació de tasques de la release");
            Release featureInDemo = new Release(12,"Incorporació de nova tasca a la release");
            releases = new Release[2];
            releases[0] = featuresOutDemo;
            releases[1] = featureInDemo;
        }
        else{
            releases = new Release[0];
            System.out.println("ProjectId incorrect");
        }
        return releases;
    }

    public Plan getPlan(String projectId, String releaseId){
        Plan plan = new Plan();
        if(Integer.parseInt(projectId) == 4 || Integer.parseInt(projectId) == 5){
            //Load json file as a plan
            ObjectMapper mapper = new ObjectMapper();
            String basePath = "src/main/java/web/plan_examples/";
            String path = "";
            int rId = Integer.parseInt(releaseId);
            if(rId >= 5 && rId <= 10){
                path = basePath + "plan_ecommerce_llibres_testing.json";
            }
            else if(rId == 11){
                path = basePath + "plan_ecommerce_llibres_demo_feature_out.json";
            }
            else if(rId == 12){
                path = basePath + "plan_ecommerce_llibres_demo_new_feature.json";
            }
            try {
                plan = mapper.readValue(new File(path),Plan.class);
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
