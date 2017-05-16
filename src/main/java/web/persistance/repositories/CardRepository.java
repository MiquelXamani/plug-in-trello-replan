package web.persistance.repositories;

import org.springframework.data.repository.CrudRepository;
import web.persistance.models.CardPersist;

/**
 * Created by Miquel on 16/05/2017.
 */
public interface CardRepository extends CrudRepository<CardPersist,String> {
}
