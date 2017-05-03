package web.persistance.repositories;


import org.springframework.data.repository.CrudRepository;
import web.persistance.models.LogPersist;

import java.util.List;

public interface LogRepository extends CrudRepository<LogPersist, Integer> {
    List<LogPersist> findAll();
}
