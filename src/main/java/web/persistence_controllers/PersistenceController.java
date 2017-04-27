package web.persistence_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import web.domain.Board;
import web.domain.Label;
import web.domain.ListTrello;
import web.persistance.models.BoardPersist;
import web.persistance.models.Endpoint;
import web.persistance.models.LabelPersist;
import web.persistance.models.ListTrelloPersist;
import web.persistance.repositories.BoardRepository;
import web.persistance.repositories.EndpointRepository;
import web.persistance.repositories.LabelRepository;
import web.persistance.repositories.ListTrelloRepository;

import java.util.ArrayList;
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


    public void saveBoard(Board board, List<Label> labels, List<ListTrello> lists){
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

        //save db
        boardRepository.save(boardPersist);

        List<ListTrelloPersist> dblists = listTrelloRepository.findAll();
        for(ListTrelloPersist l : dblists){
            System.out.println(l.getName());
            System.out.println(l.getId());
        }
    }

    public boolean isReadyList(String idList){
        List<ListTrelloPersist> listTrelloPersist = listTrelloRepository.findByIdAndName(idList,"Done");
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
}
