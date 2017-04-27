package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.LabelPersist;

import java.util.List;

/**
 * Created by Miquel on 20/04/2017.
 */
public interface LabelRepository extends CrudRepository<LabelPersist,String> {
    List<LabelPersist> findAll();
    LabelPersist findByColorAndIdBoard(String color, String boardId);
}
