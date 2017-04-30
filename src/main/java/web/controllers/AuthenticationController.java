package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.domain.User2;
import web.persistance.repositories.UserRepository;
import web.persistence_controllers.PersistenceController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {
    private PersistenceController persistenceController;

    @Autowired
    public AuthenticationController(PersistenceController persistenceController){
        this.persistenceController = persistenceController;
    }

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody Map<String,String> authInfo){
        String username = authInfo.get("username");
        String password = authInfo.get("password");
        User2 user = persistenceController.getUser(username);
        Map<String,String> errorInfo = new HashMap<>();
        if(user == null){
            errorInfo.put("description","User doesn't exist");
            return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
        }
        else if(!user.getPassword().equals(password) ) {
            errorInfo.put("description","Incorrect password");
            return new ResponseEntity<>(errorInfo, HttpStatus.FORBIDDEN);
        }
        else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }
}
