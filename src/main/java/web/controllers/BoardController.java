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
@RequestMapping("/board")
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
        User u = userRepository.findByUsername(planBoardDTO.getUsername());
        String trelloToken = u.getTrelloToken();
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
            //Add member associated with the resource to the board
            if(!resourcesAddedBoard.containsKey(resourceId)){
                ResourceMember resourceMember = resourceMemberRepository.findByUserIdAndResourceId(u.getUserId(),resourceId);
                if(resourceMember != null){
                    String trelloUserId = resourceMember.getTrelloUserId();
                    trelloService.addMemberToBoard(board.getId(),trelloUserId,trelloToken);
                    resourcesAddedBoard.put(resourceId,trelloUserId);
                }
            }
            //Add member to a card if already exists a card for the feature
            feature = j.getFeature();
            featureId = feature.getId();
            if(featuresConverted.containsKey(featureId)){
                card = featuresConverted.get(featureId);
                card.getIdMembers().add(Integer.toString(resourceId));
            }
            //Otherwise, create a card for the feature
            else{
                card = new Card();
                String name = "("+feature.getEffort()+") " + feature.getName();
                card.setName(name);
                card.setDue(feature.getDeadline());
                card.getIdMembers().add(Integer.toString(resourceId));
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
                for (Job j2: j.getDepends_on()) {
                    description += " " + j2.getFeature().getName();
                }
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