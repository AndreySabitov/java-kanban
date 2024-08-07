package services;

import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTaskManager(File file) {
        this.path = file.toPath();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            List<String> tasksFromFile = Files.readAllLines(file.toPath());
            tasksFromFile.stream().skip(1)
                    .forEach(string -> {
                        String[] infAboutTask = string.split(",");
                        taskManager.restoreCount(infAboutTask[0]);
                        switch (TasksTypes.valueOf(infAboutTask[1])) {
                            case TASK:
                                taskManager.restoreTask(infAboutTask);
                                break;
                            case EPIC:
                                taskManager.restoreEpic(infAboutTask);
                                break;
                            case SUBTASK:
                                taskManager.restoreSubtask(infAboutTask);
                        }
                    });
        } catch (IOException exception) {
            throw new ManagerLoadException("Ошибка загрузки из файла");
        }
        return taskManager;
    }

    private void restoreCount(String str) {
        int number = Integer.parseInt(str);
        if (number >= count) {
            count = number + 1;
        }
    }

    private void restoreTask(String[] infAboutTask) {
        Task task = new Task(Integer.parseInt(infAboutTask[0]), infAboutTask[2], infAboutTask[4],
                TaskStatuses.valueOf(infAboutTask[3]), Duration.ofMinutes(Long.parseLong(infAboutTask[5])),
                LocalDateTime.parse(infAboutTask[6], Task.FORMATTER));
        tasks.put(task.getTaskId(), task);
        prioritizedTaskList.add(task);
    }

    private void restoreEpic(String[] infAboutTask) {
        Epic epic = new Epic(Integer.parseInt(infAboutTask[0]), infAboutTask[2], infAboutTask[4]);
        epic.setStatus(TaskStatuses.valueOf(infAboutTask[3]));
        epic.setDuration(Duration.ofMinutes(Long.parseLong(infAboutTask[5])));
        if (infAboutTask.length == 8) {
            epic.setStartTime(LocalDateTime.parse(infAboutTask[6], Task.FORMATTER));
            epic.setEndTime(LocalDateTime.parse(infAboutTask[7], Task.FORMATTER));
        }
        epics.put(epic.getTaskId(), epic);
    }

    private void restoreSubtask(String[] infAboutTask) {
        Subtask subtask = new Subtask(Integer.parseInt(infAboutTask[5]), Integer.parseInt(infAboutTask[0]),
                infAboutTask[2], infAboutTask[4], TaskStatuses.valueOf(infAboutTask[3]),
                Duration.ofMinutes(Long.parseLong(infAboutTask[6])), LocalDateTime.parse(infAboutTask[7],
                Task.FORMATTER));
        subtasks.put(subtask.getTaskId(), subtask);
        prioritizedTaskList.add(subtask);
        Epic epic = epics.get(subtask.getIdOfEpic());
        epic.saveSubtaskId(subtask.getTaskId());
    }

    public void save() {
        try (FileWriter writer = new FileWriter(String.valueOf(path), StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic,duration,startTime,endTime\n");
            getTasksList().stream()
                    .map(Task::toStringFile)
                    .forEach(record -> {
                        try {
                            writer.write(record + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка сохранения в файл");
                        }
                    });
            getEpicsList().stream()
                    .map(Epic::toStringFile)
                    .forEach(record -> {
                        try {
                            writer.write(record + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка сохранения в файл");
                        }
                    });
            getSubtasksList().stream()
                    .map(Subtask::toStringFile)
                    .forEach(record -> {
                        try {
                            writer.write(record + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка сохранения в файл");
                        }
                    });
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(Integer id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    public Path getPath() {
        return path;
    }
}