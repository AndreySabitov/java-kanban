package services;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected Integer count = 0;
    protected TreeSet<Task> prioritizedTaskList = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected List<Task> temporaryTaskStorage = new ArrayList<>();

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
        calcAttributesOfEpic(epic);
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
            calcAttributesOfEpic(epic);
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
            calcAttributesOfEpic(epic);
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
            epic.getSubtaskIds().stream()
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
            calcAttributesOfEpic(epic);
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteTasks() {
        tasks.keySet().stream()
                .forEach(historyManager::remove);
        tasks.values().stream()
                .forEach(task -> prioritizedTaskList.remove(task));
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.keySet().stream()
                .forEach(historyManager::remove);
        subtasks.values().stream()
                .forEach(subtask -> prioritizedTaskList.remove(subtask));
        subtasks.clear();
        epics.values().stream()
                .forEach(epic -> {
                    epic.cleanSubtaskIds();
                    calcAttributesOfEpic(epic);
                });
    }

    @Override
    public void deleteEpics() {
        subtasks.keySet().stream()
                .forEach(historyManager::remove);
        subtasks.values().stream()
                .forEach(subtask -> prioritizedTaskList.remove(subtask));
        subtasks.clear();
        epics.keySet().stream()
                .forEach(historyManager::remove);
        epics.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void calcAttributesOfEpic(Epic epic) {
        ArrayList<Integer> subtasksIds = epic.getSubtaskIds();
        epic.setStatus(checkStatus(subtasksIds));
        epic.setDuration(calcDurationOfEpic(subtasksIds));
        epic.setStartTime(calcStartTimeOfEpic(subtasksIds));
        epic.setEndTime(calcEndTimeOfEpic(subtasksIds));
    }

    protected Duration calcDurationOfEpic(ArrayList<Integer> subtasksIds) {
        if (subtasksIds.isEmpty()) {
            return Duration.ofMinutes(0);
        }
        Optional<Long> minutes = subtasksIds.stream()
                .map(subtasks::get)
                .map(Task::getDuration)
                .map(Duration::toMinutes)
                .reduce(Long::sum);
        return Duration.ofMinutes(minutes.get());
    }

    protected LocalDateTime calcStartTimeOfEpic(ArrayList<Integer> subtasksIds) {
        if (subtasksIds.isEmpty()) {
            return null;
        }
        Optional<LocalDateTime> startTime = subtasksIds.stream()
                .map(subtasks::get)
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo);
        return startTime.get();
    }

    protected LocalDateTime calcEndTimeOfEpic(ArrayList<Integer> subtasksIds) {
        if (subtasksIds.isEmpty()) {
            return null;
        }
        Optional<Subtask> endSubtask = subtasksIds.stream()
                .map(subtasks::get)
                .max(Comparator.comparing(Task::getStartTime));
        return endSubtask.map(Task::getEndTime).orElse(null);
    }

    @Override
    public TreeSet<Task> getPrioritizedTaskList() {
        return new TreeSet<>(prioritizedTaskList);
    }

    protected boolean checkTimeIntersectionOfTasks(Task task) {
        boolean result;
        if (task.getStartTime() != null && task.getDuration() != null) {
            result = prioritizedTaskList.stream()
                    .noneMatch(task1 -> (task.getStartTime().isAfter(task1.getStartTime()) &&
                            task.getStartTime().isBefore(task1.getEndTime())) ||
                            (task.getEndTime().isAfter(task1.getStartTime()) &&
                                    task.getEndTime().isBefore(task1.getEndTime())));
        } else {
            result = false;
        }
        if (!result) {
            temporaryTaskStorage.add(task);
        }
        return result;
    }
}