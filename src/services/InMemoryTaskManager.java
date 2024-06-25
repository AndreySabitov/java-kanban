package services;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private Integer count = 0;

    private Integer setId() {
        return count++;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
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

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubTask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int addNewTask(Task task) {
        Integer id = setId();
        task.setTaskId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
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

    @Override
    public int addNewSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getIdOfEpic()) && !epics.containsKey(subtask.getTaskId())) {
            int id = setId();
            subtask.setTaskId(id);
            subtasks.put(id, subtask);
            Epic epic = epics.get(subtask.getIdOfEpic());
            epic.saveSubtaskId(id);
            epic.setStatus(checkStatus(epic.getSubtaskIds()));
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getTaskId())) {
            tasks.replace(task.getTaskId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getTaskId())) {
            int id = epic.getTaskId();
            epics.get(id).setTaskName(epic.getTaskName());
            epics.get(id).setTaskDescription(epic.getTaskDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getTaskId())) {
            int id = subtask.getTaskId();
            subtasks.replace(id, subtask);
            Epic epic = epics.get(subtask.getIdOfEpic());
            epic.setStatus(checkStatus(epic.getSubtaskIds()));
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Integer> subtasksIds = epic.getSubtaskIds();
            for (Integer subtasksId : subtasksIds) {
                subtasks.remove(subtasksId);
                historyManager.remove(subtasksId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int idOfEpic = subtask.getIdOfEpic();
            Epic epic = epics.get(idOfEpic);
            ArrayList<Integer> subtasksOfEpicIds = epic.getSubtaskIds();
            subtasksOfEpicIds.remove(id);
            epic.setStatus(checkStatus(subtasksOfEpicIds));
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            epic.setStatus(checkStatus(epic.getSubtaskIds()));
        }
    }

    @Override
    public void deleteEpics() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}