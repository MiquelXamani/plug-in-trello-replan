package web.models;

/**
 * Created by Miquel on 10/03/2017.
 */
public class ResourceDTO {
    private String id;
    private String name;

    public ResourceDTO(){}

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
}
