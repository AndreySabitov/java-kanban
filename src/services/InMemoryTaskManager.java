package services;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected Integer count = 0;
    protected TreeSet<Task> prioritizedTaskList = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
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
        if (checkTimeIntersectionOfTasks(task)) {
            Integer id = setId();
            task.setTaskId(id);
            tasks.put(id, task);
            prioritizedTaskList.add(task);
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public int addNewEpic(Epic epic) {
        Integer id = setId();
        epic.setTaskId(id);
        epic.cleanSubtaskIds();
        epic.setStatus(checkStatus(epic.getSubtaskIds()));
        setTimeOfEpic(epic);
        epics.put(id, epic);
        return id;
    }

    protected TaskStatuses checkStatus(ArrayList<Integer> subtasksIds) {
        int countNew;
        int countDone = 0;
        List<Subtask> subtasksOfEpic;
        subtasksOfEpic = subtasksIds.stream()
                .map(subtasks::get)
                .toList();
        if (subtasksOfEpic.isEmpty()) {
            return TaskStatuses.NEW;
        }
        countNew = (int) subtasksOfEpic.stream()
                .filter(subtask -> subtask.getStatus() == TaskStatuses.NEW)
                .count();
        countDone = (int) subtasksOfEpic.stream()
                .filter(subtask -> subtask.getStatus() == TaskStatuses.DONE)
                .count();
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
        if (epics.containsKey(subtask.getIdOfEpic()) && !epics.containsKey(subtask.getTaskId())
                && checkTimeIntersectionOfTasks(subtask)) {
            int id = setId();
            subtask.setTaskId(id);
            subtasks.put(id, subtask);
            prioritizedTaskList.add(subtask);
            Epic epic = epics.get(subtask.getIdOfEpic());
            epic.saveSubtaskId(id);
            epic.setStatus(checkStatus(epic.getSubtaskIds()));
            setTimeOfEpic(epic);
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getTaskId()) && checkTimeIntersectionOfTasks(task)) {
            Task taskToRemove = tasks.get(task.getTaskId());
            prioritizedTaskList.remove(taskToRemove);
            tasks.replace(task.getTaskId(), task);
            prioritizedTaskList.add(task);
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
        if (subtask != null && subtasks.containsKey(subtask.getTaskId()) && checkTimeIntersectionOfTasks(subtask)) {
            int id = subtask.getTaskId();
            Subtask subtaskToRemove = subtasks.get(id);
            prioritizedTaskList.remove(subtaskToRemove);
            subtasks.replace(id, subtask);
            prioritizedTaskList.add(subtask);
            Epic epic = epics.get(subtask.getIdOfEpic());
            epic.setStatus(checkStatus(epic.getSubtaskIds()));
            setTimeOfEpic(epic);
        }
    }

    @Override
    public void deleteTask(int id) {
        Task taskToRemove = tasks.get(id);
        prioritizedTaskList.remove(taskToRemove);
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epic.getSubtaskIds()
                    .forEach(integer -> {
                        Subtask subtaskToRemove = subtasks.get(integer);
                        prioritizedTaskList.remove(subtaskToRemove);
                        subtasks.remove(integer);
                        historyManager.remove(integer);
                    });
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            prioritizedTaskList.remove(subtask);
            int idOfEpic = subtask.getIdOfEpic();
            Epic epic = epics.get(idOfEpic);
            ArrayList<Integer> subtasksOfEpicIds = epic.getSubtaskIds();
            subtasksOfEpicIds.remove(id);
            epic.setStatus(checkStatus(subtasksOfEpicIds));
            setTimeOfEpic(epic);
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteTasks() {
        tasks.values()
                .forEach(task -> {
                    historyManager.remove(task.getTaskId());
                    prioritizedTaskList.remove(task);
                });
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.values()
                .forEach(subtask -> {
                    historyManager.remove(subtask.getTaskId());
                    prioritizedTaskList.remove(subtask);
                });
        subtasks.clear();
        epics.values()
                .forEach(epic -> {
                    epic.cleanSubtaskIds();
                    epic.setStatus(checkStatus(epic.getSubtaskIds()));
                    setTimeOfEpic(epic);
                });
    }

    @Override
    public void deleteEpics() {
        subtasks.values()
                .forEach(subtask -> {
                    historyManager.remove(subtask.getTaskId());
                    prioritizedTaskList.remove(subtask);
                });
        subtasks.clear();
        epics.keySet()
                .forEach(historyManager::remove);
        epics.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void setTimeOfEpic(Epic epic) {
        if (!epic.getSubtaskIds().isEmpty()) {
            List<Subtask> subtasksOfEpic = getEpicSubtasks(epic.getTaskId());
            subtasksOfEpic.sort(Comparator.comparing(Task::getStartTime));
            Subtask startSubtask = subtasksOfEpic.getFirst();
            Subtask endSubtask = subtasksOfEpic.getLast();
            epic.setDuration(Duration.ofMinutes(subtasksOfEpic.stream()
                    .map(Task::getDuration)
                    .map(Duration::toMinutes)
                    .reduce(Long::sum).get()));
            epic.setStartTime(startSubtask.getStartTime());
            epic.setEndTime(endSubtask.getEndTime());
        } else {
            epic.setDuration(Duration.ofMinutes(0));
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTaskList);
    }

    protected boolean checkTimeIntersectionOfTasks(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null) {
            return prioritizedTaskList.stream()
                    .noneMatch(task1 -> (task.getStartTime().isAfter(task1.getStartTime()) &&
                            task.getStartTime().isBefore(task1.getEndTime())) ||
                            (task.getEndTime().isAfter(task1.getStartTime()) &&
                                    task.getEndTime().isBefore(task1.getEndTime())));
        } else {
            return false;
        }
    }
}