package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.BoardPersist;


public interface BoardRepository extends CrudRepository<BoardPersist,String> {
}
