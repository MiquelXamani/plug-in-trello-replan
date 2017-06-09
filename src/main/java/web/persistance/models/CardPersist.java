package web.persistance.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import web.domain.Board;
import web.domain.Log;

import javax.persistence.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class CardPersist {
    @Id
    private String id;
    private String name;
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<LogPersist> logs;
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<JobPersist> jobs;
    @ManyToOne
    @JoinColumn(name = "boardId")
    private BoardPersist board;
    private boolean accepted;
    private boolean rejected;
    private boolean alive;

    public CardPersist(){}

    public CardPersist(String id, String name, BoardPersist boardPersist){
        this.id = id;
        this.name = name;
        this.board = boardPersist;
        this.accepted = true;
        this.rejected = false;
        this.accepted = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LogPersist> getLogs() {
        return logs;
    }

    public void setLogs(List<LogPersist> logs) {
        this.logs = logs;
    }

    public void addLog(LogPersist logPersist){
        logs.add(logPersist);
    }

    public List<JobPersist> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobPersist> jobs) {
        this.jobs = jobs;
    }

    public BoardPersist getBoard() {
        return board;
    }

    public void setBoard(BoardPersist board) {
        this.board = board;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
