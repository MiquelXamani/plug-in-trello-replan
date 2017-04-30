package web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import web.persistance.models.ResourceMember;
import web.persistance.models.UserPersist;
import web.persistance.repositories.BoardRepository;
import web.persistance.repositories.ResourceMemberRepository;
import web.persistance.repositories.UserRepository;
import web.persistence_controllers.PersistenceController;

@Component
public class InitialDataLoader implements ApplicationRunner {

    private UserRepository userRepository;
    private ResourceMemberRepository resourceMemberRepository;
    private PersistenceController persistenceController;

    @Autowired
    public InitialDataLoader(UserRepository userRepository, ResourceMemberRepository resourceMemberRepository, PersistenceController persistenceController) {
        this.userRepository = userRepository;
        this.resourceMemberRepository = resourceMemberRepository;
        this.persistenceController = persistenceController;
    }

    public void run(ApplicationArguments args) {
        UserPersist user = new UserPersist("Miquel","123","b9af2c827b36369367e5416dcccb657a949f4745f1b41ee7f70d2fe91f78165e","miquelxamani1","585bec12bec126f26851c2fb");
        userRepository.save(user);

        persistenceController.saveEndpoint("https://lit-savannah-17077.herokuapp.com/api/ui/v1","Development: Heroku Deployment");
        //persistenceController.saveEndpoint("","Development: Local");

        //ResourceMember resourceMember = new ResourceMember(new Long(1),1,"Josep","589b03b9b6ee4d99b0da3dca","josep248","Josep");
        //resourceMemberRepository.save(resourceMember);

        ResourceMember resourceMember2 = new ResourceMember(new Long(1),2,"Miquel Xamani","Miquel Xamaní Moreno","585bec12bec126f26851c2fb","miquelxamani1","Miquel Xamani");
        resourceMemberRepository.save(resourceMember2);

        ResourceMember resourceMember3 = new ResourceMember(new Long(1),3,"Miquel","Miquel Munford","5870fd94eabd62f19de4ef5f","miquel133","Miquel");
        resourceMemberRepository.save(resourceMember3);
    }
}