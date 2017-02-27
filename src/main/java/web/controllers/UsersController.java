package web.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.models.User;
import web.repositories.UserRepository;

@RestController
@RequestMapping("/users")
public class UsersController {
    UserRepository userRepository;

    @RequestMapping(method= RequestMethod.GET)
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
