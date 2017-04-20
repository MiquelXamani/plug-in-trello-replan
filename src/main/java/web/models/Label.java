package web.models;

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
public class Label {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "idBoard")
    private Board board;
    private String color;

    public Label (){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
