package web.persistence_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import web.domain.Board;
import web.domain.Label;
import web.persistance.models.BoardPersist;
import web.persistance.models.LabelPersist;
import web.persistance.repositories.BoardRepository;
import web.persistance.repositories.LabelRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miquel on 24/04/2017.
 */
public class BoardPersistenceController {
    @Autowired(required = true)
    private BoardRepository boardRepository;

    @Autowired(required = true)
    private LabelRepository labelRepository;

    public BoardPersistenceController(){}

    public void saveBoard(Board board, List<Label> labels){
        BoardPersist boardPersist = new BoardPersist(board.getId(),board.getName(),board.getUrl());
        List<LabelPersist> labelPersists = new ArrayList<>();
        for (Label label: labels) {
            LabelPersist labelPersist = new LabelPersist(label.getId(),boardPersist,label.getColor());
            labelPersists.add(labelPersist);
        }
        boardPersist.setLabels(labelPersists);
        boardRepository.save(boardPersist);
        List<LabelPersist> test = labelRepository.findAll();
        System.out.println(test.size());
    }
}
