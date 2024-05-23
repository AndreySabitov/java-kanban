package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;

    public Epic(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        subtaskIds = new ArrayList<>();
    }

    public Epic(Integer taskId, String taskName, String taskDescription) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        subtaskIds = new ArrayList<>();
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "taskName='" + taskName + "', " +
                "subtasksIds=" + subtaskIds;
        if (taskDescription != null) {
            result = result + ", taskDescription.length='" + taskDescription.length() + "'";
        }
        return result = result + ", taskId=" + taskId +
                ", status=" + status +
                '}';
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void saveSubtaskId(int id) {
        subtaskIds.add(id);
    }
}
