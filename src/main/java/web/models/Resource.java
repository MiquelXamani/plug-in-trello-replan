package web.models;

/**
 * Created by Miquel on 10/03/2017.
 */
public class Resource implements Comparable<Resource>{
    private int id;
    private String name;

    public Resource(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Resource other) {
        return this.name.compareTo(other.getName());
    }
}
