package web.repositories;

import org.springframework.data.repository.CrudRepository;
import web.models.Board;


public interface BoardRepository extends CrudRepository<Board,String> {
}
