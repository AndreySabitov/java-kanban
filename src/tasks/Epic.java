package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    public Epic(String taskName, String taskDescription, int taskId) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskId = taskId;
    }

    /*public Epic(String taskName, String taskDescription, int taskId) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskId = taskId;
    }*/

    public TaskStatuses checkStatus(ArrayList<SubTask> subtasks) {
        int countNew = 0;
        int countDone = 0;
        if (subtasks.isEmpty()) {
            return TaskStatuses.NEW;
        }
        for (SubTask task: subtasks) {
            if (task.status == TaskStatuses.NEW) {
                countNew++;
            } else if (task.status == TaskStatuses.DONE) {
                countDone++;
            }
        }
        if (countNew == subtasks.size()) {
            return TaskStatuses.NEW;
        } else if (countDone == subtasks.size()) {
            return TaskStatuses.DONE;
        } else {
            return TaskStatuses.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "taskName='" + taskName + "'";
        if (taskDescription != null) {
            result = result + ", taskDescription.length='" + taskDescription.length() + "'";
        }
        return result = result + ", taskId=" + taskId +
                ", status=" + status +
                '}';
    }
}
