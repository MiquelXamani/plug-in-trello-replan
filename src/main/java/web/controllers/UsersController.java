package web.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.models.User;

@RestController
@RequestMapping("/card")
public class UsersController {

    @RequestMapping(method= RequestMethod.GET)
    public User getUserByUsername(String username){
        return new User();
    }
}
