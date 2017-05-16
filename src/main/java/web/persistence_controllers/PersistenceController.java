package web.persistence_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.LogType;
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
    @Autowired (required = true)
    private CardRepository cardRepository;
    @Autowired (required = true)
    private JobRepository jobRepository;

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

    public List<Endpoint> getEndpoints(){
        return endpointRepository.findAll();
    }

    public Endpoint getEndpoint(int endpointId){
        return endpointRepository.findOne(endpointId);
    }

    public Endpoint saveEndpoint(String url, String name){
        return endpointRepository.save(new Endpoint(url,name));
    }

    public String getLabelId(String boardId, String color){
        LabelPersist labelPersist = labelRepository.findByColorAndBoardId(color,boardId);
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

    private String createLogDescription(String cardName, String memberUsername,LogType type){
        String description = "";
        switch (type){
            case FINISHED_EARLIER:
                description = cardName+" marked as finished by "+memberUsername+" earlier than expected";
                break;
            case FINISHED_LATE:
                description = cardName+" marked as finished by "+memberUsername+" later than expected";
                break;
            case MOVED_TO_IN_PROGRESS:
                description = cardName+" moved to in progress by "+memberUsername;
                break;
            case MOVED_TO_READY:
                description = cardName+" moved to ready by "+memberUsername;
                break;
            case REJECTED:
                description = cardName+" rejected by "+memberUsername;
                break;
            default:
                break;
        }
        return description;
    }

    public Log saveLog(String boardId, String boardName, String cardId, String cardName, String memberUsername, LogType type){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String createdAt = dateFormat.format(date);
        String description = createLogDescription(cardName,memberUsername,type);
        Log log = new Log(createdAt, boardId, boardName, cardId, cardName, type, description);
        BoardPersist boardPersist = boardRepository.findOne(boardId);
        CardPersist cardPersist = cardRepository.findOne(cardId);
        LogPersist logPersist = new LogPersist(createdAt,false,boardPersist,cardPersist,memberUsername,type.value,description);
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
            LogType logType;
            for (BoardPersist b: userPersist.getBoards()) {
                for(LogPersist lp: b.getLogs()){
                    logType = LogType.getEnum(lp.getType());
                    CardPersist cp = lp.getCard();
                    log = new Log(lp.getId(),lp.getCreatedAt(),b.getId(),b.getName(),cp.getId(),cp.getName(),lp.getAccepted(),logType,lp.getDescription());
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
            LogType logType;
            for(LogPersist lp: boardPersist.getLogs()){
                logType = LogType.getEnum(lp.getType());
                CardPersist cp = lp.getCard();
                log = new Log(lp.getId(),lp.getCreatedAt(),boardPersist.getId(),boardPersist.getName(),cp.getId(),cp.getName(),lp.getAccepted(),logType,lp.getDescription());
                logs.add(log);
            }
        }
        return logs;
    }

    public Board getBoard(String boardId){
        BoardPersist boardPersist = boardRepository.findOne(boardId);
        return new Board (boardPersist.getId(),boardPersist.getName(),boardPersist.getUrl());
    }

    public Log setAcceptedLog(int logId, boolean accepted){
        LogPersist lp = logRepository.findOne(logId);
        lp.setAccepted(accepted);
        logRepository.save(lp);
        LogType logType = LogType.getEnum(lp.getType());
        CardPersist cp = lp.getCard();
        return new Log(lp.getId(),lp.getCreatedAt(),lp.getBoard().getId(),lp.getBoard().getName(),cp.getId(),cp.getName(),lp.getAccepted(),logType,lp.getDescription());
    }

    public void saveCardAndJobs(Card card, List<Job> jobs){
        CardPersist cardPersist = new CardPersist(card.getId(),card.getName());
        List<JobPersist> jobPersists = new ArrayList<>();
        for(Job j : jobs){
            jobPersists.add(new JobPersist(j.getId(),cardPersist));
        }
        cardPersist.setJobs(jobPersists);

        cardRepository.save(cardPersist);

    }
}
