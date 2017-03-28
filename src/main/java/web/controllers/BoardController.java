package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import web.models.Board;
import web.models.ListTrello;
import web.models.PlanBoardDTO;
import web.models.User;
import web.repositories.BoardRepository;
import web.repositories.ListTrelloRepository;
import web.repositories.UserRepository;
import web.services.TrelloService;

import java.util.List;

@RestController
@RequestMapping("/board")
public class BoardController {
    @Autowired(required = true)
    private UserRepository userRepository;
    @Autowired(required = true)
    private BoardRepository boardRepository;
    @Autowired(required = true)
    private ListTrelloRepository listTrelloRepository;

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<Board> createBoard(@RequestBody PlanBoardDTO planBoardDTO){
        User u = userRepository.findByUsername(planBoardDTO.getUsername());
        TrelloService trelloService = new TrelloService();

        Board board = trelloService.createBoard(planBoardDTO.getName(),planBoardDTO.getBoardName(),planBoardDTO.getTeamId(),u.getTrelloToken());
        boardRepository.save(board);

        List<ListTrello> lists = trelloService.createLists(board.getId(),u.getTrelloToken());
        listTrelloRepository.save(lists);




        //provisional
        return new ResponseEntity<>(board,HttpStatus.MULTI_STATUS);
    }
}
