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

    @RequestMapping(method= RequestMethod.GET)
    public Team[] getTeams(@RequestParam(value = "username") String username){
        User u = userRepository.findByUsername(username);
        String trelloUsername = u.getTrelloUsername();
        String trelloToken = u.getTrelloToken();
        TrelloService trelloService = new TrelloService();
        //només interessa obtenir la id per poder fer la següent crida que és la que permet saber els noms dels usuaris
        Team[] teams = trelloService.getTrelloTeams(trelloUsername,trelloToken);
        Team[] result = new Team[teams.length];
        for(int i = 0; i < teams.length; i++){
            result[i] = trelloService.getTrelloTeamMembers(teams[i].getId(),trelloToken);
        }
        return result;
    }
}
