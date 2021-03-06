package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.dtos.User2;
import web.domain_controllers.DomainController;
import web.services.TrelloService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {
    private DomainController domainController;

    @Autowired
    public UsersController(DomainController domainController){
        this.domainController = domainController;
    }
/*
    @RequestMapping(method= RequestMethod.GET)//s'ha de passar paràmetre user i si retorna buit vol dir que no ha trobat cap usuari amb aquest username
    public User getUserByUsername(@RequestParam(value = "username") String username){
        return userRepository.findByUsername(username);
    }*/

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
    public ResponseEntity<Object> createUser(@RequestBody User2 user){
            User2 u = domainController.getUser(user.getUsername());
            if(u == null){
                //crida api trello per saber username
                TrelloService trelloService = new TrelloService();
                String trelloUserUsername = trelloService.getTrelloUserUsername(user.getTrelloToken());
                System.out.println("Trello username: " + trelloUserUsername);
                user.setTrelloUsername(trelloUserUsername);

                System.out.println("Usertoken: " + user.getTrelloToken());

                User2 userCreated = domainController.saveUser(user);
                return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
            }
            else {
                Map<String,String> error = new HashMap<>(1);
                error.put("description","Username is already taken");
                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
            }



    }
}
