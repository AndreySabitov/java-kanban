package tasks;

import java.util.Objects;

public class Task {
    protected String taskName;
    protected String taskDescription;
    protected int taskId;
    protected TaskStatuses status;

    public Task() {
    }

    public Task(String taskName, String taskDescription, int taskId, TaskStatuses status) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskId = taskId;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId &&
                Objects.equals(taskName, task.taskName) &&
                Objects.equals(taskDescription, task.taskDescription) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        int hash = 11;
        if (taskName != null) {
            hash = taskName.hashCode() + hash;
        }
        hash = hash * 31;
        if (taskDescription != null) {
            hash = hash + taskDescription.hashCode();
        }
        hash = hash * 17;
        if (status != null) {
            hash = hash + status.hashCode();
        }
        return hash + taskId;
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "taskName='" + taskName + "'";
        if (taskDescription != null) {
            result = result + ", taskDescription.length='" + taskDescription.length() + "'";
        }
        return result = result + ", taskId=" + taskId +
                ", status=" + status +
                '}';
    }

    public int getTaskId() {
        return taskId;
    }

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }
}
