package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.models.PlanBoardDTO;
import web.models.User;
import web.repositories.ResourceMemberRepository;
import web.repositories.UserRepository;

@RestController
@RequestMapping("/board")
public class BoardController {
    @Autowired(required = true)
    private UserRepository userRepository;
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<Object> createBoard(@RequestBody PlanBoardDTO planBoardDTO){
        User u = userRepository.findByUsername(planBoardDTO.getUsername());

        //provisional
        return new ResponseEntity<Object>(HttpStatus.MULTI_STATUS);
    }
}
