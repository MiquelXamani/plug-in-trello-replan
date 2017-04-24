package web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miquel on 08/03/2017.
 */
public class TeamWithMembers extends Team{
    private List<Member> members;

    TeamWithMembers(){
        this.members = new ArrayList<>();
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
