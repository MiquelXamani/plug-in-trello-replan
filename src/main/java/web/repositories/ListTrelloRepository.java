package web.repositories;

import org.springframework.data.repository.CrudRepository;
import web.models.ListTrello;


public interface ListTrelloRepository extends CrudRepository<ListTrello, String> {
}
