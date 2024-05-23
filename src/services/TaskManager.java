package services;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private Integer count = 0;

    public Integer setId() {
        return count++;
    }

    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Subtask> getEpicSubtasks(int epicId) {
        if (!epics.containsKey(epicId)) {
            return null;
        }
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer id : epic.getSubtaskIds()) {
            subtasksOfEpic.add(subtasks.get(id));
        }
        return subtasksOfEpic;
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubTask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public int addNewTask(Task task) {
        Integer id = setId();
        task.setTaskId(id);
        tasks.put(id, task);
        return id;
    }

    public int addNewEpic(Epic epic) {
        Integer id = setId();
        epic.setTaskId(id);
        epic.cleanSubtaskIds();
        epic.setStatus(checkStatus(epic.getSubtaskIds()));
        epics.put(id, epic);
        return id;
    }

    private TaskStatuses checkStatus(ArrayList<Integer> subtasksIds) {
        int countNew = 0;
        int countDone = 0;
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer id : subtasksIds) {
            Subtask subtask = subtasks.get(id);
            subtasksOfEpic.add(subtask);
        }
        if (subtasksOfEpic.isEmpty()) {
            return TaskStatuses.NEW;
        }
        for (Subtask task : subtasksOfEpic) {
            if (task.getStatus() == TaskStatuses.NEW) {
                countNew++;
            } else if (task.getStatus() == TaskStatuses.DONE) {
                countDone++;
            }
        }
        if (countNew == subtasksOfEpic.size()) {
            return TaskStatuses.NEW;
        } else if (countDone == subtasksOfEpic.size()) {
            return TaskStatuses.DONE;
        } else {
            return TaskStatuses.IN_PROGRESS;
        }
    }

    public Integer addNewSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getIdOfEpic())) {
            int id = setId();
            subtask.setTaskId(id);
            subtasks.put(id, subtask);
            Epic epic = epics.get(subtask.getIdOfEpic());
            epic.saveSubtaskId(id);
            epic.setStatus(checkStatus(epic.getSubtaskIds()));
            return id;
        } else {
            return null;
        }
    }

    public void updateTask(Task task) {
        if (task != null) {
            tasks.replace(task.getTaskId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getTaskId())) {
            int id = epic.getTaskId();
            epics.get(id).setTaskName(epic.getTaskName());
            epics.get(id).setTaskDescription(epic.getTaskDescription());
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getTaskId())) {
            int id = subtask.getTaskId();
            subtasks.replace(id, subtask);
            Epic epic = epics.get(subtask.getIdOfEpic());
            epic.setStatus(checkStatus(epic.getSubtaskIds()));
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Integer> subtasksIds = epic.getSubtaskIds();
            for (Integer subtasksId : subtasksIds) {
                subtasks.remove(subtasksId);
            }
            epics.remove(id);
        }
    }

    public void deleteSubtask(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int idOfEpic = subtask.getIdOfEpic();
            Epic epic = epics.get(idOfEpic);
            ArrayList<Integer> subtasksOFEpicIds = epic.getSubtaskIds();
            subtasksOFEpicIds.remove(id);
            epic.setStatus(checkStatus(subtasksOFEpicIds));
            subtasks.remove(id);
        }
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            epic.setStatus(checkStatus(epic.getSubtaskIds()));
        }
    }

    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }
}