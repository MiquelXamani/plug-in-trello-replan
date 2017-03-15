package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.models.MatchingDTO;
import web.models.ResourceMember;
import web.models.User;
import web.repositories.ResourceMemberRepository;
import web.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/match")
public class MatchingController {
    @Autowired(required = true)
    private UserRepository userRepository;
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<Object> matchResourceWithMember(@RequestBody MatchingDTO matchingDTO){
        User u = userRepository.findByUsername(matchingDTO.getUsername());
        ResourceMember resourceMember = new ResourceMember(u.getUserId(),matchingDTO.getResourceId(),matchingDTO.getResourceName(),
                matchingDTO.getTrelloUserId(),matchingDTO.getTrelloUsername(),matchingDTO.getTrelloFullName());
        try {
            resourceMember = resourceMemberRepository.save(resourceMember);
            return new ResponseEntity<>(resourceMember, HttpStatus.CREATED);
        }
        catch (DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>(1);
            error.put("description", "Already exists an association for this resource or team member");
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
    }
}
