package web.persistence_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.domain.*;
import web.persistance.models.*;
import web.persistance.repositories.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class PersistenceController {
    @Autowired(required = true)
    private BoardRepository boardRepository;
    @Autowired(required = true)
    private LabelRepository labelRepository;
    @Autowired(required = true)
    private ListTrelloRepository listTrelloRepository;
    @Autowired(required = true)
    private EndpointRepository endpointRepository;
    @Autowired (required = true)
    private UserRepository userRepository;
    @Autowired (required = true)
    private LogRepository logRepository;


    public void saveBoard(Board board, List<Label> labels, List<ListTrello> lists, User2 user2){
        //create board
        BoardPersist boardPersist = new BoardPersist(board.getId(),board.getName(),board.getUrl());

        //create labels
        List<LabelPersist> labelPersists = new ArrayList<>();
        LabelPersist labelPersist;
        for (Label label: labels) {
            labelPersist = new LabelPersist(label.getId(),boardPersist,label.getColor());
            labelPersists.add(labelPersist);
        }
        boardPersist.setLabels(labelPersists);

        //create lists
        List<ListTrelloPersist> listTrelloPersists = new ArrayList<>();
        ListTrelloPersist listTrelloPersist;
        for(ListTrello listTrello: lists){
            listTrelloPersist = new ListTrelloPersist(listTrello.getId(),listTrello.getName(),boardPersist);
            listTrelloPersists.add(listTrelloPersist);
        }
        boardPersist.setLists(listTrelloPersists);

        UserPersist user = userRepository.findByUsername(user2.getUsername());
        user.addBoard(boardPersist);
        boardPersist.setUser(user);

        //save db
        boardRepository.save(boardPersist);

        List<ListTrelloPersist> dblists = listTrelloRepository.findAll();
        for(ListTrelloPersist l : dblists){
            System.out.println(l.getName());
            System.out.println(l.getId());
        }
    }

    public User2 getBoardUser(String boardId){
        UserPersist userPersist = boardRepository.findOne(boardId).getUser();
        User2 user = new User2(userPersist.getUsername(),userPersist.getPassword(),userPersist.getTrelloToken(),userPersist.getTrelloUsername(),userPersist.getTrelloUserId());
        user.setUserId(userPersist.getUserId());
        return user;
    }

    public boolean isDoneList(String boardId, String idList){
        //TODO: ha d'obtenir un sol objecte, no una llista
        List<ListTrelloPersist> listTrelloPersist = listTrelloRepository.findByIdAndNameAndBoardId(idList,"Done",boardId);
        if(listTrelloPersist.size() > 0){
            return true;
        }
        else return false;
    }

    public List<Endpoint> getEndpoints(){
        return endpointRepository.findAll();
    }

    public Endpoint getEndpoint(int endpointId){
        return endpointRepository.findOne(endpointId);
    }

    public Endpoint saveEndpoint(String url, String name){
        return endpointRepository.save(new Endpoint(url,name));
    }

    public String getGreenLabelId(String boardId){
        LabelPersist labelPersist = labelRepository.findByColorAndBoardId("green",boardId);
        return labelPersist.getId();
    }

    public String getPurpleLabelId(String boardId){
        LabelPersist labelPersist = labelRepository.findByColorAndBoardId("purple",boardId);
        return labelPersist.getId();
    }

    public String getYellowLabelId(String boardId){
        LabelPersist labelPersist = labelRepository.findByColorAndBoardId("yellow",boardId);
        return labelPersist.getId();
    }

    public String getListId(String boardId, String name){
        return listTrelloRepository.findByNameAndBoardId(name,boardId).getId();
    }

    public User2 getUser(String username){
        UserPersist user = userRepository.findByUsername(username);
        if(user == null) return null;
        User2 user2 = new User2(user.getUsername(),user.getPassword(),user.getTrelloToken(),user.getTrelloUsername(),user.getTrelloUserId());
        user2.setUserId(user.getUserId());
        return user2;
    }

    public User2 saveUser(User2 user2){
        UserPersist user = new UserPersist(user2.getUsername(),user2.getPassword(),user2.getTrelloToken(),user2.getTrelloUsername(),user2.getTrelloUserId());
        user = userRepository.save(user);
        user2.setUserId(user.getUserId());
        return user2;
    }

    public List<Board> getUserBoards(String username){
        UserPersist userPersist = userRepository.findByUsername(username);
        List<Board> boards = new ArrayList<>();
        if(userPersist != null) {
            List<BoardPersist> boardPersists = userPersist.getBoards();
            System.out.println("Number of boards found: " + boardPersists.size());
            for (BoardPersist bp : boardPersists) {
                boards.add(new Board(bp.getId(), bp.getName(), bp.getUrl()));
            }
        }
        return boards;
    }

    public Log saveFinishedEarlierLog(String boardId, String cardId, String cardName, String memberUsername){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String createdAt = dateFormat.format(date);
        String type = "finished_earlier";
        String description = cardName+" marked as finished by "+memberUsername+" earlier than expected";
        Log log = new Log(createdAt, boardId, cardId, cardName, type, description);
        BoardPersist boardPersist = boardRepository.findOne(boardId);
        LogPersist logPersist = new LogPersist(createdAt,false,boardPersist,cardId,cardName,memberUsername,type,description);
        boardPersist.addLog(logPersist);
        boardRepository.save(boardPersist);
        log.setId(logPersist.getId());
        return log;
    }

    public List<Log> getLogs(String username){
        //mirar si es pot fer de forma més eficient
        UserPersist userPersist = userRepository.findByUsername(username);
        List<Log> logs = new ArrayList<>();
        if(userPersist != null){
            Log log;
            for (BoardPersist b: userPersist.getBoards()) {
                for(LogPersist lp: b.getLogs()){
                    log = new Log(lp.getId(),lp.getCreatedAt(),b.getId(),b.getName(),lp.getCardId(),lp.getCardName(),lp.getRead(),lp.getType(),lp.getDescription());
                    logs.add(log);
                }

            }
        }
        return logs;
    }

    public List<Log> getBoardLogs(String boardId){
        BoardPersist boardPersist = boardRepository.findOne(boardId);
        List<Log> logs = new ArrayList<>();
        if(boardPersist != null){
            Log log;
            for(LogPersist lp: boardPersist.getLogs()){
                log = new Log(lp.getId(),lp.getCreatedAt(),boardPersist.getId(),boardPersist.getName(),lp.getCardId(),lp.getCardName(),lp.getRead(),lp.getType(),lp.getDescription());
                logs.add(log);
            }
        }
        return logs;
    }
}
