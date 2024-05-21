package tasks;

public class SubTask extends Task {
    int idOfEpic;

    public SubTask(String taskName, String taskDescription, int taskId, TaskStatuses status, int idOfEpic) {
        super(taskName, taskDescription, taskId, status);
        this.idOfEpic = idOfEpic;
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
