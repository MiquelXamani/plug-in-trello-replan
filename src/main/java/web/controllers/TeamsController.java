package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web.controllers.APITrello.TrelloService;
import web.models.Team;
import web.models.User;
import web.repositories.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamsController {
    @Autowired(required = true)
    private UserRepository userRepository;

    @RequestMapping(method= RequestMethod.GET)//s'ha de passar paràmetre user i si retorna buit vol dir que no ha trobat cap usuari amb aquest username
    public Team[] getUserByUsername(@RequestParam(value = "username") String username){
        User u = userRepository.findByUsername(username);
        String trelloUsername = u.getTrelloUsername();
        String trelloToken = u.getTrelloToken();
        TrelloService trelloService = new TrelloService();
        return trelloService.getTrelloTeam(trelloUsername,trelloToken);
    }
}
