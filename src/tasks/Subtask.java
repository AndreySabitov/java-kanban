package tasks;

public class Subtask extends Task {
    private final int idOfEpic;

    public Subtask(int idOfEpic, String taskName, String taskDescription, TaskStatuses status) {
        super(taskName, taskDescription, status);
        this.idOfEpic = idOfEpic;
    }

    public Subtask(int idOfEpic, Integer taskId, String taskName, String taskDescription, TaskStatuses status) {
        this.idOfEpic = idOfEpic;
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
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
                '}';
    }
}
