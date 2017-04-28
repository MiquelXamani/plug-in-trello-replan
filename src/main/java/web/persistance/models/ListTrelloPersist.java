package web.persistance.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.groups.ConvertGroup;

@Table(
        uniqueConstraints= {
                @UniqueConstraint(columnNames = {"name", "idBoard"})
        }
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class ListTrelloPersist {
    @Id
    private String id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "idBoard")
    private BoardPersist board;

    public ListTrelloPersist(){}

    public ListTrelloPersist(String id, String name, BoardPersist board){
        this.id = id;
        this.name = name;
        this.board = board;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BoardPersist getBoard() {
        return board;
    }

    public void setBoard(BoardPersist board) {
        this.board = board;
    }
}
