package web.persistence_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.LogType;
import web.domain.*;
import web.persistance.fake_models.FeatureFake;
import web.persistance.fake_models.JobFake;
import web.persistance.fake_models.PlanFake;
import web.persistance.fake_models.ResourceFake;
import web.persistance.fake_repositories.FeatureFakeRepository;
import web.persistance.fake_repositories.JobFakeRepository;
import web.persistance.fake_repositories.PlanFakeRepository;
import web.persistance.fake_repositories.ResourceFakeRepository;
import web.persistance.models.*;
import web.persistance.repositories.*;

import java.text.SimpleDateFormat;
import java.util.*;

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
    //Fake repositories
    @Autowired (required = true)
    private PlanFakeRepository planFakeRepository;
    @Autowired (required = true)
    private JobFakeRepository jobFakeRepository;
    @Autowired (required = true)
    private FeatureFakeRepository featureFakeRepository;
    @Autowired (required = true)
    private ResourceFakeRepository resourceFakeRepository;

    public void saveBoard(Board board, List<Label> labels, List<ListTrello> lists, User2 user2, int endpointId, int projectId, int releaseId){
        System.out.println(endpointId+" "+projectId+" "+releaseId);

        //create board
        BoardPersist boardPersist = new BoardPersist(board.getId(),board.getName(),board.getUrl(), projectId,releaseId);

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

        EndpointPersist endpoint = endpointRepository.findOne(endpointId);
        endpoint.addBoard(boardPersist);
        boardPersist.setEndpoint(endpoint);

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
        List<EndpointPersist> endpointPersists = endpointRepository.findAll();
        List<Endpoint> endpoints = new ArrayList<>();
        for(EndpointPersist endpointPersist:endpointPersists){
            endpoints.add(new Endpoint(endpointPersist.getId(),endpointPersist.getUrl(),endpointPersist.getName()));
        }
        return endpoints;
    }

    public Endpoint getEndpoint(int endpointId){
        EndpointPersist endpointPersist = endpointRepository.findOne(endpointId);
        return new Endpoint(endpointPersist.getId(),endpointPersist.getUrl(),endpointPersist.getName());
    }

    public Endpoint saveEndpoint(String url, String name){
        EndpointPersist endpointPersist = endpointRepository.save(new EndpointPersist(url,name));
        return new Endpoint(endpointPersist.getId(),endpointPersist.getUrl(),endpointPersist.getName());
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
        LogPersist logPersist = new LogPersist(createdAt,false,false,boardPersist,cardPersist,memberUsername,type.value,description);
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
                    log = new Log(lp.getId(),lp.getCreatedAt(),b.getId(),b.getName(),cp.getId(),cp.getName(),lp.getAccepted(),lp.isRejected(),logType,lp.getDescription());
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
                log = new Log(lp.getId(),lp.getCreatedAt(),boardPersist.getId(),boardPersist.getName(),cp.getId(),cp.getName(),lp.getAccepted(),lp.isRejected(),logType,lp.getDescription());
                logs.add(log);
            }
        }
        return logs;
    }

    public Board getBoard(String boardId){
        BoardPersist boardPersist = boardRepository.findOne(boardId);
        return new Board (boardPersist.getId(),boardPersist.getName(),boardPersist.getUrl());
    }

    public Log setAcceptedFinishedLog(int logId, boolean accepted){
        LogPersist lp = logRepository.findOne(logId);
        lp.setAccepted(accepted);
        logRepository.save(lp);
        LogType logType = LogType.getEnum(lp.getType());
        CardPersist cp = lp.getCard();
        return new Log(lp.getId(),lp.getCreatedAt(),lp.getBoard().getId(),lp.getBoard().getName(),cp.getId(),cp.getName(),lp.getAccepted(),lp.isRejected(),logType,lp.getDescription());
    }

    public Log setRejectedPreviousFinishedLog(String cardId){
        List<String> types = new ArrayList<>();
        types.add(LogType.FINISHED_EARLIER.value);
        types.add(LogType.FINISHED_LATE.value);
        LogPersist lp = logRepository.findFirstByCardIdAndTypeInOrderByIdDesc(cardId,types);
        lp.setRejected(true);
        lp.setAccepted(false);
        logRepository.save(lp);
        LogType logType = LogType.getEnum(lp.getType());
        CardPersist cp = lp.getCard();
        return new Log(lp.getId(),lp.getCreatedAt(),lp.getBoard().getId(),lp.getBoard().getName(),cp.getId(),cp.getName(),lp.getAccepted(),lp.isRejected(),logType,lp.getDescription());
    }

    public void saveCardAndJobs(Card card, List<Job> jobs, String boardId){
        CardPersist cardPersist = new CardPersist(card.getId(),card.getName());
        List<JobPersist> jobPersists = new ArrayList<>();
        Feature feature;
        BoardPersist boardPersist = boardRepository.findOne(boardId);
        for(Job j : jobs){
            feature = j.getFeature();
            jobPersists.add(new JobPersist(j.getId(),cardPersist,feature.getId(),feature.getEffort(),feature.getName(),boardPersist));
        }
        cardPersist.setJobs(jobPersists);

        cardRepository.save(cardPersist);

    }

    public List<Integer> getJobsIdsLog(int logId){
       List<JobPersist> jobsPersists = logRepository.findOne(logId).getCard().getJobs();
       List<Integer> jobsIds = new ArrayList<>();
       for(JobPersist jp:jobsPersists){
           jobsIds.add(jp.getId());
       }
       return jobsIds;
    }

    public Map<String,String> getBoardReplanInfoFromLogId(int logId){
        BoardPersist bp = logRepository.findOne(logId).getBoard();
        Map<String,String> boardInfo = new HashMap<>();
        boardInfo.put("project",String.valueOf(bp.getProjectId()));
        boardInfo.put("release",String.valueOf(bp.getReleaseId()));
        boardInfo.put("endpoint",bp.getEndpoint().getUrl());
        boardInfo.put("endpointId",String.valueOf(bp.getEndpoint().getId()));
        return boardInfo;
    }

    private String getListNameFromLogType(String logTypeString){
        LogType logType = LogType.getEnum(logTypeString);
        String listName = "";
        switch (logType){
            case REJECTED:
                listName = "In Progress";
                break;
            case FINISHED_LATE:
                listName = "Done";
                break;
            case FINISHED_EARLIER:
                listName = "Done";
                break;
            case MOVED_TO_READY:
                listName = "Ready";
                break;
            case MOVED_TO_IN_PROGRESS:
                listName = "In Progress";
                break;
            default:
                break;
        }
        return listName;
    }

    public List<CardTrackingInfo> getCardTrackingInfo(String cardId){
        //List<LogPersist> logPersists = cardRepository.findOne(cardId).getLogs();
        List<LogPersist> logPersists = logRepository.findByCardIdOrderById(cardId);
        List<CardTrackingInfo> cardTrackingInfos = new ArrayList<>();
        String listName;
        for(LogPersist lp:logPersists){
            listName = getListNameFromLogType(lp.getType());
            cardTrackingInfos.add(new CardTrackingInfo(lp.getId(),lp.getMemberUsername(),lp.getCreatedAt(),listName,LogType.getEnum(lp.getType())));
        }
        return cardTrackingInfos;
    }

    public String getCardId(int featureId, int endpointId, int projectId, int releaseId){
        return jobRepository.findFirstByFeatureIdAndBoardEndpointIdAndBoardReleaseIdAndBoardProjectId(featureId,endpointId,releaseId,projectId).getCard().getId();
        //return jobRepository.findFirstByFeatureId(featureId).getCard().getId();
    }

    public List<Integer> getInProgressJobs(List<String> inProgressCardsId){
        List<JobPersist> jobPersistList = jobRepository.findByCardIdIn(inProgressCardsId);
        List<Integer> inProgressJobsIds = new ArrayList<>();
        for(JobPersist j:jobPersistList){
            inProgressJobsIds.add(j.getJobId());
        }
        return inProgressJobsIds;
    }

    public Feature getFeature(int featureId, int endpointId, int projectId, int releaseId){
        JobPersist jobPersist = jobRepository.findFirstByFeatureIdAndBoardEndpointIdAndBoardReleaseIdAndBoardProjectId(featureId,endpointId,releaseId,projectId);
        return new Feature(jobPersist.getFeatureId(),jobPersist.getFeatureName(),jobPersist.getFeatureEffort());
    }

    public void savePlan(Plan p){
        PlanFake planFake = new PlanFake(p.getId(),p.getCreated_at());
        List<Job> jobs = p.getJobs();
        Feature f;
        Resource r;
        List<JobReduced> jrs;
        FeatureFake featureFake;
        ResourceFake resourceFake;
        JobFake previousJobFake;
        JobFake jobFake;
        Map<Integer,JobFake> jobFakeMap = new HashMap<>();
        Map<Integer,ResourceFake> resourceFakeMap = new HashMap<>();
        for(Job j:jobs){
            jobFake = new JobFake(j.getId(),j.getStarts(),j.getEnds(),planFake);

            f = j.getFeature();
            featureFake = featureFakeRepository.findOne(f.getId());
            if(featureFake == null){
                featureFake = new FeatureFake(f.getId(),f.getName(),f.getDescription(),f.getEffort(),f.getDeadline());
            }
            jobFake.setFeature(featureFake);
            featureFake.addJob(jobFake);

            r = j.getResource();
            //resourceFake = resourceFakeMap.get(r.getId());
            resourceFake = resourceFakeRepository.findOne(r.getId());
            if(resourceFake == null){
                resourceFake = new ResourceFake(r.getId(),r.getName(),r.getDescription());
                //resourceFakeMap.put(r.getId(),resourceFake);
            }
            jobFake.setResource(resourceFake);
            resourceFake.addJob(jobFake);

            jrs = j.getDepends_on();
            for(JobReduced previous:jrs){
                previousJobFake = jobFakeMap.get(previous.getId());
                if(previousJobFake != null){
                    jobFake.addPrevious(previousJobFake);
                    previousJobFake.addNext(jobFake);
                }
            }
            jobFakeRepository.save(jobFake);
            jobFakeMap.put(jobFake.getId(),jobFake);
        }
        planFake.setJobs(new ArrayList<>(jobFakeMap.values()));
        planFakeRepository.save(planFake);

        /*List<FeatureFake> foundFeatures = featureFakeRepository.findAll();
        int count = 0;
        for(FeatureFake fk:foundFeatures){
            System.out.println(count + " "+fk.getName());
            count ++;
        }
        System.out.println(jobFakeRepository.findOne(6).getPrevious().get(0).getId());*/

    }
}
