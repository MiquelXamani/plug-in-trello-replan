package web.persistance.repositories;


import org.springframework.data.repository.CrudRepository;
import web.persistance.models.UserPersist;

public interface UserRepository extends CrudRepository<UserPersist, Long> {

    UserPersist findByUsername(String username);
}