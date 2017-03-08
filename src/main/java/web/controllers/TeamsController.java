package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web.controllers.APITrello.TrelloService;
import web.models.Team;
import web.models.TeamWithMembers;
import web.models.User;
import web.repositories.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamsController {
    @Autowired(required = true)
    private UserRepository userRepository;

    //retorna els noms dels teams de Trello als qual pertany l'usuari
    @RequestMapping(method= RequestMethod.GET)
    public Team[] getTeams(@RequestParam(value = "username") String username){
        User u = userRepository.findByUsername(username);
        String trelloUsername = u.getTrelloUsername();
        String trelloToken = u.getTrelloToken();
        TrelloService trelloService = new TrelloService();
        Team[] teams = trelloService.getTrelloTeams(trelloUsername,trelloToken);
        return teams;
    }

    @RequestMapping(value="/members", method=RequestMethod.GET)
    public TeamWithMembers getTeamMembers(@RequestParam(value = "username") String username,
                                          @RequestParam(value = "unmatchedMembersOnly", defaultValue = "false")String unmatchedMembersOnly,
                                          @RequestParam(value = "teamId") String teamId){
        User u = userRepository.findByUsername(username);
        String trelloToken = u.getTrelloToken();
        TrelloService trelloService = new TrelloService();
        TeamWithMembers teamWithMembers = trelloService.getTrelloTeamMembers(teamId,trelloToken);
        return teamWithMembers;
        //aquesta funció retornarà els membres d'un equip: els que no estan relacionats amb cap recurs si unmatchedMembersOnly és true
        //tots altrament
    }
}
