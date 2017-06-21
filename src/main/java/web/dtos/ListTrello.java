package web.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Created by Miquel on 28/03/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class ListTrello {
    private String id;
    private String name;
    private String idBoard;

    public ListTrello(){};

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

    public String getIdBoard() {
        return idBoard;
    }

    public void setIdBoard(String idBoard) {
        this.idBoard = idBoard;
    }
}
