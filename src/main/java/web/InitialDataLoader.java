package web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import web.models.User;
import web.repositories.UserRepository;

@Component
public class InitialDataLoader implements ApplicationRunner {

    private UserRepository userRepository;

    @Autowired
    public InitialDataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void run(ApplicationArguments args) {
        User user = new User("Miquel","123","b9af2c827b36369367e5416dcccb657a949f4745f1b41ee7f70d2fe91f78165e","miquelxamani1","585bec12bec126f26851c2fb");
        userRepository.save(user);
    }
}