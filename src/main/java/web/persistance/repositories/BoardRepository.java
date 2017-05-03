package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.BoardPersist;

import java.util.List;


public interface BoardRepository extends CrudRepository<BoardPersist,String> {
}
