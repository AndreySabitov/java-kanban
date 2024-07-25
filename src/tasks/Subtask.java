package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Integer idOfEpic;

    public Subtask(int idOfEpic, String taskName, String taskDescription, TaskStatuses status, Duration duration,
                   LocalDateTime startTime) {
        super(taskName, taskDescription, status, duration, startTime);
        this.idOfEpic = idOfEpic;
    }

    public Subtask(int idOfEpic, Integer taskId, String taskName, String taskDescription, TaskStatuses status,
                   Duration duration, LocalDateTime startTime) {
        this.idOfEpic = idOfEpic;
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "idOfEpic=" + idOfEpic +
                ", taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + startTime.format(FORMATTER) +
                '}';
    }

    public String toStringFile() {
        return String.join(",", taskId.toString(), TasksTypes.SUBTASK.toString(), taskName,
                status.toString(), taskDescription, idOfEpic.toString(), String.valueOf(duration.toMinutes()),
                startTime.format(FORMATTER));
    }
}
