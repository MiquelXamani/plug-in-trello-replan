package web.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import web.models.User;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByUsername(String username);
}