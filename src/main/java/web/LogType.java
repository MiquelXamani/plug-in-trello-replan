package web;

public enum LogType {
    FINISHED_EARLIER("finished_earlier"),
    FINISHED_LATE("finished_late"),
    MOVED_TO_IN_PROGRESS("moved_to_in_progress"),
    MOVED_TO_READY("moved_to_ready"),
    REJECTED("rejected");

    public final String value;

    LogType(String value){
        this.value = value;
    }

    public static LogType getEnum(String name){
        for(LogType l : LogType.values()){
            if(l.value.equals(name)){
                return l;
            }
        }
        return null;
    }


}
