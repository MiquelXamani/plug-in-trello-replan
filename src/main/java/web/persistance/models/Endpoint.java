package web.persistance.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import web.persistance.repositories.BoardRepository;

import javax.persistence.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Endpoint {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String url;
    private String name;
    @OneToMany(mappedBy = "endpoint")
    private List<BoardPersist> boards;

    public Endpoint(){}

    public Endpoint(String url, String name){
        this.url = url;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BoardPersist> getBoard() {
        return boards;
    }

    public void setBoard(List<BoardPersist> boards) {
        this.boards = boards;
    }

    public void addBoard(BoardPersist board){
        this.boards.add(board);
    }
}
