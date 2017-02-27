package web.controllers;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import web.models.User;
import web.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

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
        try {
            User userCreated = userRepository.save(user);
            return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
        }
        catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
