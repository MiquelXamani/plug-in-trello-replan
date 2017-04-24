package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.LabelPersist;

/**
 * Created by Miquel on 20/04/2017.
 */
public interface LabelRepository extends CrudRepository<LabelPersist,String> {
}
