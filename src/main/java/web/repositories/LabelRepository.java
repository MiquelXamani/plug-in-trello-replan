package web.repositories;

import org.springframework.data.repository.CrudRepository;
import web.models.Label;

/**
 * Created by Miquel on 20/04/2017.
 */
public interface LabelRepository extends CrudRepository<Label,String> {
}
