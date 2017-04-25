package web.persistence_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import web.domain.Board;
import web.domain.Label;
import web.persistance.models.BoardPersist;
import web.persistance.models.LabelPersist;
import web.persistance.repositories.BoardRepository;
import web.persistance.repositories.LabelRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class BoardPersistenceController {
    @Autowired(required = true)
    private BoardRepository boardRepository;
    @Autowired(required = true)
    private LabelRepository labelRepository;


    public void saveBoard(Board board, List<Label> labels){
        BoardPersist boardPersist = new BoardPersist(board.getId(),board.getName(),board.getUrl());
        List<LabelPersist> labelPersists = new ArrayList<>();
        LabelPersist labelPersist;
        for (Label label: labels) {
            labelPersist = new LabelPersist(label.getId(),boardPersist,label.getColor());
            labelPersists.add(labelPersist);
        }
        boardPersist.setLabels(labelPersists);
        boardRepository.save(boardPersist);
        List<LabelPersist> test = labelRepository.findAll();
        System.out.println(test.size());
    }
}
