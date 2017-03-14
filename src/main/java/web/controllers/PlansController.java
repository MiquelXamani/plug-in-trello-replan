package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.models.Plan;
import web.models.Resource;
import web.models.ResourceMember;
import web.models.User;
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
    public Plan[] getPlans(@RequestBody String username){
        //aquí aniria la crida al replan

        //per simular la crida al replan


    }

    @RequestMapping(value="/resources",method= RequestMethod.POST)
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
    }


}
