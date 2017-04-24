package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.ListTrelloPersist;


public interface ListTrelloRepository extends CrudRepository<ListTrelloPersist, String> {
}
