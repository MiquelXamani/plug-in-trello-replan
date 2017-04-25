package web.persistance.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by Miquel on 30/03/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class LabelPersist {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "idBoard")
    private BoardPersist board;
    private String color;

    public LabelPersist (){}

    public LabelPersist(String id, BoardPersist board, String color){
        this.id = id;
        this.board = board;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BoardPersist getBoard() {
        return board;
    }

    public void setBoard(BoardPersist board) {
        this.board = board;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
