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
    public ResponseEntity<Board> createBoard(@RequestBody PlanBoardDTO planBoardDTO){
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
        int resourceId;
        for (Job j: jobs) {
            resourceId = j.getResource().getId();
            if(!resourcesAddedBoard.containsKey(resourceId)){
                ResourceMember resourceMember = resourceMemberRepository.findByUserIdAndResourceId(u.getUserId(),resourceId);
                if(resourceMember != null){
                    String trelloUserId = resourceMember.getTrelloUserId();
                    trelloService.addMemberToBoard(board.getId(),trelloUserId,trelloToken);
                    resourcesAddedBoard.put(resourceId,trelloUserId);
                }
            }
            //crear objecte card si no exiteix cap card per la feature o modificar l'existent
        }






        //provisional
        return new ResponseEntity<>(board,HttpStatus.MULTI_STATUS);
    }
}
