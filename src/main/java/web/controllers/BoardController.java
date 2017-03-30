package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.models.*;
import web.repositories.BoardRepository;
import web.repositories.ListTrelloRepository;
import web.repositories.ResourceMemberRepository;
import web.repositories.UserRepository;
import web.services.TrelloService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/boards")
public class BoardController {
    @Autowired(required = true)
    private UserRepository userRepository;
    @Autowired(required = true)
    private BoardRepository boardRepository;
    @Autowired(required = true)
    private ListTrelloRepository listTrelloRepository;
    @Autowired(required = true)
    private ResourceMemberRepository resourceMemberRepository;

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<PlanTrello> createBoard(@RequestBody PlanBoardDTO planBoardDTO){
        System.out.println("BOARD CONTROLLER REQUEST RECEIVED");
        User u = userRepository.findByUsername(planBoardDTO.getUsername());
        String trelloToken = u.getTrelloToken();
        String trelloUserIdWebUser = u.getTrelloUserId();
        TrelloService trelloService = new TrelloService();

        //Create board
        Board board = trelloService.createBoard(planBoardDTO.getName(),planBoardDTO.getBoardName(),planBoardDTO.getTeamId(),trelloToken);
        boardRepository.save(board);

        //Create lists
        List<ListTrello> lists = trelloService.createLists(board.getId(),trelloToken);
        listTrelloRepository.save(lists);

        List<Job> jobs = planBoardDTO.getJobs();
        //Map of resourceId and trelloUserId
        Map<Integer,String> resourcesAddedBoard = new HashMap<>();
        //Map of featureId and card corresponding to the feature
        Map<Integer,Card> featuresConverted = new HashMap<>();
        int resourceId, featureId;
        Card card;
        Feature feature;
        for (Job j: jobs) {
            resourceId = j.getResource().getId();
            String trelloUserId = "";
            //Add member associated with the resource to the board
            if(!resourcesAddedBoard.containsKey(resourceId)){
                ResourceMember resourceMember = resourceMemberRepository.findByUserIdAndResourceId(u.getUserId(),resourceId);
                if(resourceMember != null){
                    trelloUserId = resourceMember.getTrelloUserId();
                    if(!trelloUserId.equals(trelloUserIdWebUser)) {
                        //si es dóna el cas que l'usuari de la web està a la planificació, no es pot convidar al board ja que n'és el propietari
                        trelloService.addMemberToBoard(board.getId(), trelloUserId, trelloToken);
                    }
                }
                resourcesAddedBoard.put(resourceId,trelloUserId);
            }
            //Add member to a card if already exists a card for the feature
            feature = j.getFeature();
            featureId = feature.getId();
            //si el recurs ja està en el map, es recupera
            if(trelloUserId.equals("")){
                trelloUserId = resourcesAddedBoard.get(resourceId);
            }

            if(featuresConverted.containsKey(featureId)){
                card = featuresConverted.get(featureId);
                //si el recurs està associat a un usuari s'assigna a la card
                if(!trelloUserId.equals("")) {
                    card.getIdMembers().add(trelloUserId);
                }
            }
            //Otherwise, create a card for the feature
            else{
                card = new Card();
                String name = "("+ Math.round(feature.getEffort()) +") " + feature.getName();
                card.setName(name);
                card.setDue(feature.getDeadline());
                //si el recurs està associat a un usuari s'assigna a la card
                if(!trelloUserId.equals("")) {
                    card.getIdMembers().add(trelloUserId);
                }
                if(j.getDepends_on().isEmpty()){
                    //Ready
                    card.setIdList(lists.get(2).getId());
                }
                else{
                    //On-hold
                    card.setIdList(lists.get(1).getId());
                }
                String description = feature.getDescription() + "\n\n";
                description += "**Start date:** " + j.getStarts() + "\n**Depends on:**";
                if(j.getDepends_on().size() == 0){
                    description += " -";
                }
                else {
                    for (Job j2 : j.getDepends_on()) {
                        description += " " + j2.getFeature().getName();
                    }
                }
                card.setDesc(description);
                //card.setIdLabels();
                featuresConverted.put(featureId,card);

            }
        }

        List<Card> cards = new ArrayList<>(featuresConverted.values());
        trelloService.createCards(cards,trelloToken);

        PlanTrello result = new PlanTrello();
        result.setBoard(board);
        result.setLists(lists);
        result.setCards(cards);

        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
