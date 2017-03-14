package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.models.*;
import web.repositories.ResourceMemberRepository;
import web.repositories.UserRepository;

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

    @RequestMapping(method=RequestMethod.GET)
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
        //Job1
        Feature f1 = new Feature();
        f1.setId(1);
        f1.setName("Tasca 1");
        f1.setDescription("Aquesta serà la descripció de la tasca 1");
        f1.setEffort(1);
        Job j1 = new Job();
        j1.setEnds("23/03/2017");
        j1.setFeature(f1);
        List<Resource> resourcesJ1 = new ArrayList<>();
        resourcesJ1.add(r1);
        j1.setResources(resourcesJ1);
        //Job 2
        Feature f2 = new Feature();
        f2.setId(2);
        f2.setName("Tasca 2");
        f2.setDescription("Aquesta serà la descripció de la tasca 2");
        f2.setEffort(13);
        Job j2 = new Job();
        j2.setEnds("30/03/2017");
        j2.setFeature(f2);
        List<Resource> resourcesJ2 = new ArrayList<>();
        resourcesJ2.add(r2);
        resourcesJ2.add(r3);
        resourcesJ2.add(r4);
        j2.setResources(resourcesJ2);
        List<Job> jobsP1 = new ArrayList<>();
        jobsP1.add(j1);
        jobsP1.add(j2);
        p1.setJobs(jobsP1);
        plans[0] = p1;

        //Plan 2
        Plan p2 = new Plan();
        p2.setCreated_at("29/01/2017");
        p2.setId(2);
        p2.setName("Release Febrer");
        Feature f3 = new Feature();
        f3.setId(3);
        f3.setName("Tasca 3");
        f3.setDescription("Aquesta serà la descripció de la tasca 3");
        f3.setEffort(5);
        Job j3 = new Job();
        j3.setEnds("23/03/2017");
        j3.setFeature(f3);
        List<Resource> resourcesJ3 = new ArrayList<>();
        resourcesJ3.add(r1);
        resourcesJ3.add(r4);
        j3.setResources(resourcesJ3);
        List<Job> jobsP3 = new ArrayList<>();
        jobsP3.add(j3);
        p2.setJobs(jobsP3);
        plans[1] = p2;

        return plans;
    }

    /*@RequestMapping(value="/resources",method= RequestMethod.POST)
    public ResponseEntity<Object> getUnlinkedResources(@RequestBody Plan plan){
        String username = plan.getUsername();
        User user = userRepository.findByUsername(username);
        Long userId = user.getUserId();
        List<String> resourcesIds = new ArrayList<>();
        for(int i = 0; i < plan.getResources().size(); i++){
            resourcesIds.add(plan.getResources().get(i).getId());
        }
        Collections.sort(resourcesIds);
        List<ResourceMember> resourcesFound = resourceMemberRepository.findByUserIdAndResourceIdIn(userId,resourcesIds);

        List<Resource> notFoundMembers = new ArrayList<>();
        //falta trobar els que no estan assignats
        return new ResponseEntity<>(user, HttpStatus.OK);
    }*/


}
