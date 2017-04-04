package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.models.*;
import web.repositories.ResourceMemberRepository;
import web.repositories.UserRepository;
import web.services.ReplanService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/plans")
public class PlansController {
    @Autowired(required = true)
    private UserRepository userRepository;
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;

    /*@RequestMapping(method=RequestMethod.GET)
    public Plan[] getPlans(@RequestParam(value = "username") String username){
        //aquí aniria la crida al replan

        //per simular la crida al replan
        Plan[] plans = new Plan[2];
        Resource r1 = new Resource();
        r1.setId(1);
        r1.setName("Josep");
        Resource r2 = new Resource();
        r2.setId(2);
        r2.setName("Miquel Xamani");
        Resource r3 = new Resource();
        r3.setId(3);
        r3.setName("Miquel");
        Resource r4 = new Resource();
        r4.setId(4);
        r4.setName("Joan");

        //Plan 1
        Plan p1 = new Plan();
        p1.setCreated_at("27/02/2017");
        p1.setId(1);
        p1.setName("Release Març");
        //Feature 1
        Feature f1 = new Feature();
        f1.setId(1);
        f1.setName("Tasca 1");
        f1.setDescription("Aquesta serà la descripció de la tasca 1");
        f1.setEffort(1);
        f1.setDeadline("03/05/2017");
        //Job 1
        Job j1 = new Job();
        j1.setFeature(f1);
        j1.setResource(r1);
        j1.setStarts("03/01/2017");
        //Feature 2
        Feature f2 = new Feature();
        f2.setId(2);
        f2.setName("Tasca 2");
        f2.setDescription("Aquesta serà la descripció de la tasca 2");
        f2.setEffort(13);
        f2.setDeadline("03/23/2017");
        //Job 2
        Job j2 = new Job();
        j2.setFeature(f2);
        j2.setResource(r2);
        j2.setStarts("03/05/2017");
        //Job 3
        Job j3 = new Job();
        j3.setFeature(f2);
        j3.setResource(r3);
        j3.setStarts("03/05/2017");
        //Job 4
        Job j4 = new Job();
        j4.setFeature(f2);
        j4.setResource(r4);
        j4.setStarts("03/05/2017");

        List<Job> jobsP1 = new ArrayList<>();
        jobsP1.add(j1);
        jobsP1.add(j2);
        jobsP1.add(j3);
        jobsP1.add(j4);
        p1.setJobs(jobsP1);
        plans[0] = p1;

        //Plan 2
        Plan p2 = new Plan();
        p2.setCreated_at("01/29/2017");
        p2.setId(2);
        p2.setName("Release Febrer");
        //Feature 3
        Feature f3 = new Feature();
        f3.setId(3);
        f3.setName("Tasca 3");
        f3.setDescription("Aquesta serà la descripció de la tasca 3");
        f3.setEffort(5);
        f3.setDeadline("02/12/2017");
        //Job 5
        Job j5 = new Job();
        j5.setFeature(f3);
        j5.setResource(r1);
        //Job 6
        Job j6 = new Job();
        j6.setFeature(f3);
        j6.setResource(r2);

        List<Job> jobsP3 = new ArrayList<>();
        jobsP3.add(j5);
        jobsP3.add(j6);
        p2.setJobs(jobsP3);
        plans[1] = p2;

        return plans;
    }*/

    @RequestMapping(value="/resources",method= RequestMethod.POST)
    public List<Resource> getUnlinkedResources(@RequestBody PlanDTO planDTO){
        String username = planDTO.getUsername();
        User user = userRepository.findByUsername(username);
        Long userId = user.getUserId();
        List<Integer> resourcesIds = new ArrayList<>(); //per fer cerca a BD i comparar amb els resultats
        List<String> resourcesNames = new ArrayList<>(); //per guardar els nom dels recursos que es retornaran
        List<Job> jobs = planDTO.getJobs();
        for (Job j:jobs) {
            boolean found = false;
            int i = 0;
            int rId = j.getResource().getId();
            while(!found && i < resourcesIds.size()){
                if(rId == resourcesIds.get(i)) {
                    found = true;
                }
                i++;
            }
            if(!found){
                resourcesIds.add(rId);
                resourcesNames.add(j.getResource().getName());
            }

        }

        Collections.sort(resourcesIds);
        List<ResourceMember> resourcesFound = resourceMemberRepository.findByUserIdAndResourceIdInOrderByResourceId(userId,resourcesIds);
        List<Resource> notFoundResources = new ArrayList<>();
        if(resourcesIds.size() != resourcesFound.size()){
            int j = 0;
            for(int i = 0; i < resourcesFound.size(); i++){
                boolean found = false;
                while(!found){
                    if(resourcesIds.get(j) == resourcesFound.get(i).getResourceId()){
                        found = true;
                    }
                    else{
                        Resource resource = new Resource();
                        resource.setId(resourcesIds.get(j));
                        resource.setName(resourcesNames.get(j));
                        notFoundResources.add(resource);
                    }
                    j++;
                }

            }
            for(int k = j; k < resourcesIds.size(); k++){
                Resource resource = new Resource();
                resource.setId(resourcesIds.get(k));
                resource.setName(resourcesNames.get(k));
                notFoundResources.add(resource);
            }

        }

        Collections.sort(notFoundResources);
        return notFoundResources;
    }

    @RequestMapping(method=RequestMethod.GET)
    public Plan[] getPlans(@RequestParam(value = "username") String username){
        //Caldrà tenir una associació entre l'usuari de la web i el del Replan per obtenir les seves planificacions
        String replanUserId = "";
        ReplanService replanService = new ReplanService();
        List<Plan> planList = replanService.getPlansTest(replanUserId);
        Plan[] plans = planList.toArray(new Plan[planList.size()]);
        return plans;
    }


}
