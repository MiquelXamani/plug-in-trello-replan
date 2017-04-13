package web.models;

public class Matching {
    private Resource resource;
    private Member member;

    public Matching(){}

    public Matching(Resource resource, Member member){
        this.resource = resource;
        this.member = member;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
