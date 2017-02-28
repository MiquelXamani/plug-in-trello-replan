package web.controllers;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import web.controllers.APITrello.MembersService;
import web.models.User;
import web.repositories.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired(required = true)
    private UserRepository userRepository;

    @RequestMapping(method= RequestMethod.GET)//s'ha de passar paràmetre user i si retorna buit vol dir que no ha trobat cap usuari amb aquest username
    public User getUserByUsername(@RequestParam(value = "username") String username){
        return userRepository.findByUsername(username);
    }

    /*//funció per retornar tots els users (només per test)
    @RequestMapping(method=RequestMethod.GET)
    public List <User> getUsers(){
        List <User> users = new ArrayList<>();
        userRepository.save(new User("username","pass","tt","tu"));
        for(User user : userRepository.findAll()){
            users.add(user);
        }
        return users;
    }*/

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody User user){
            User u = userRepository.findByUsername(user.getUsername());
            if(u == null){
                //crida api trello per saber username
                MembersService membersService = new MembersService();
                String trelloUserUsername = membersService.getTrelloUserUsername(user.getTrelloToken());
                user.setTrelloUsername(trelloUserUsername);

                User userCreated = userRepository.save(user);
                return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
            }
            else {
                Map<String,String> error = new HashMap<>(1);
                error.put("description","Username is already taken");
                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
            }



    }
}
