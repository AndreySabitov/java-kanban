package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Epic(Integer taskId, String taskName, String taskDescription) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void saveSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", finishTime=" + endTime.format(FORMATTER) +
                ", taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + startTime.format(FORMATTER) +
                '}';
    }

    @Override
    public String toStringFile() {
        if (startTime != null) {
            return String.join(",", taskId.toString(), TasksTypes.EPIC.toString(), taskName,
                    status.toString(), taskDescription, String.valueOf(duration.toMinutes()),
                    startTime.format(FORMATTER), endTime.format(FORMATTER));
        } else {
            return String.join(",", taskId.toString(), TasksTypes.EPIC.toString(), taskName,
                    status.toString(), taskDescription, String.valueOf(duration.toMinutes()));
        }
    }
}
