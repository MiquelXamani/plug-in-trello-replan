package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web.services.TrelloService;
import web.models.*;
import web.repositories.ResourceMemberRepository;
import web.repositories.UserRepository;

import java.util.*;

@RestController
@RequestMapping("/teams")
public class TeamsController {
    @Autowired(required = true)
    private UserRepository userRepository;
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;

    //retorna els noms dels teams de Trello als qual pertany l'usuari
    @RequestMapping(method = RequestMethod.GET)
    public Team[] getTeams(@RequestParam(value = "username") String username) {
        User u = userRepository.findByUsername(username);
        String trelloUsername = u.getTrelloUsername();
        String trelloToken = u.getTrelloToken();
        TrelloService trelloService = new TrelloService();
        Team[] teams = trelloService.getTrelloTeams(trelloUsername, trelloToken);
        Arrays.sort(teams);
        return teams;
    }
/*
    @RequestMapping(value="/members", method=RequestMethod.GET)
    public List<Member> getTeamMembers(@RequestParam(value = "username") String username,
                                          @RequestParam(value = "unmatchedMembersOnly", defaultValue = "false")String unmatchedMembersOnly,
                                          @RequestParam(value = "teamId") String teamId){
        User u = userRepository.findByUsername(username);
        String trelloToken = u.getTrelloToken();
        Long userId = u.getUserId();
        TrelloService trelloService = new TrelloService();
        TeamWithMembers teamWithMembers = trelloService.getTrelloTeamMembers(teamId,trelloToken);
        //aquesta funció retornarà els membres d'un equip: els que no estan relacionats amb cap recurs si unmatchedMembersOnly és true
        //tots altrament
        List<Member> members = teamWithMembers.getMembers();

        //s'ha d'ordenar la llista per facilitar la cerca i quedar-nos amb els que NO estan assignats
        Collections.sort(members);

        List<String> trelloUsernames = new ArrayList<>();
        for(int i = 0; i < members.size(); i++){
            trelloUsernames.add(members.get(i).getUsername());
        }
        List<ResourceMember> foundMembers = resourceMemberRepository.findByUserIdAndTrelloUsernameInOrderByTrelloUsernameAsc(userId,trelloUsernames);

        List<Member> notFoundMembers = new ArrayList<>();

        //recórrer la llista inicial per trobar aquells membres que apareguin a la base de dades
        if(members.size() != foundMembers.size()) {
            System.out.println("MIDES DIFERENTS!!");
            int j = 0;
            for (int i = 0; i < foundMembers.size(); i++) {
                ResourceMember rm = foundMembers.get(i);
                boolean found = false;
                while (!found) {
                    Member m = members.get(j);
                    if (rm.getTrelloUsername().equals(m.getUsername())) {
                        found = true;
                    } else {
                        notFoundMembers.add(m);
                    }
                    j++;
                }
            }

            for (int k = j; k < members.size(); k++) {
                notFoundMembers.add(members.get(k));
            }
        }
        return notFoundMembers;
    }
    */

    @RequestMapping(value = "/members", method = RequestMethod.GET)
    public List<Member> getTeamMembers(@RequestParam(value = "username") String username,
                                       @RequestParam(value = "teamId") String teamId) {
        User u = userRepository.findByUsername(username);
        String trelloToken = u.getTrelloToken();
        TrelloService trelloService = new TrelloService();
        TeamWithMembers teamWithMembers = trelloService.getTrelloTeamMembers(teamId,trelloToken);
        return teamWithMembers.getMembers();
    }
}
