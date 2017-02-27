package web.repositories;


import org.springframework.data.repository.CrudRepository;
import web.models.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);
}