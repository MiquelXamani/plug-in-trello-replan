package web.persistance.repositories;


import org.springframework.data.repository.CrudRepository;
import web.persistance.models.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);
}