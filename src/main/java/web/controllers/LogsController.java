package web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web.domain.Log;
import web.persistence_controllers.PersistenceController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogsController {
    private PersistenceController persistenceController;

    @Autowired
    public LogsController(PersistenceController persistenceController){
        this.persistenceController = persistenceController;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Log> getLogs(@RequestParam(value = "username") String username, @RequestParam(value = "boardId",required=false) String boardId) {
        //all logs
        if(boardId == null){
            return persistenceController.getLogs(username);
        }
        else{
           return persistenceController.getBoardLogs(boardId);
        }
    }
}
