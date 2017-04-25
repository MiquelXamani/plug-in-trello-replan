package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.ListTrelloPersist;

import java.util.List;


public interface ListTrelloRepository extends CrudRepository<ListTrelloPersist, String> {
    List<ListTrelloPersist> findAll();
    List<ListTrelloPersist> findByIdAndName(String id, String name);
}
