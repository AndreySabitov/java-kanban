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


    @Override
    public String toString() {
        String result = "SubTask{" +
                "idOfEpic=" + idOfEpic +
                ", taskName='" + taskName + "'";
        if (taskDescription != null) {
            result = result + ", taskDescription.length='" + taskDescription.length() + "'";
        }
        return result = result + ", taskId=" + taskId +
                ", status=" + status +
                '}';
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }
}
