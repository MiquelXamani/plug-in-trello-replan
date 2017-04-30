package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web.persistence_controllers.PersistenceController;
import web.services.TrelloService;
import web.domain.*;
import web.persistance.repositories.ResourceMemberRepository;
import web.persistance.repositories.UserRepository;

import java.util.*;

@RestController
@RequestMapping("/teams")
public class TeamsController {
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;

    private PersistenceController persistenceController;

    @Autowired
    public TeamsController(PersistenceController persistenceController){
        this.persistenceController = persistenceController;
    }

    //retorna els noms dels teams de Trello als qual pertany l'usuari
    @RequestMapping(method = RequestMethod.GET)
    public Team[] getTeams(@RequestParam(value = "username") String username) {
        User2 u = persistenceController.getUser(username);
        String trelloUsername = u.getTrelloUsername();
        String trelloToken = u.getTrelloToken();
        TrelloService trelloService = new TrelloService();
        Team[] teams = trelloService.getTrelloTeams(trelloUsername, trelloToken);
        Arrays.sort(teams);
        return teams;
    }

    @RequestMapping(value = "/members", method = RequestMethod.GET)
    public List<Member> getTeamMembers(@RequestParam(value = "username") String username,
                                       @RequestParam(value = "teamId") String teamId) {
        User2 u = persistenceController.getUser(username);
        String trelloToken = u.getTrelloToken();
        TrelloService trelloService = new TrelloService();
        TeamWithMembers teamWithMembers = trelloService.getTrelloTeamMembers(teamId,trelloToken);
        return teamWithMembers.getMembers();
    }
}
