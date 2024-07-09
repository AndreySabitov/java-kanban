package services;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final Path path;

    public FileBackedTaskManager(File file) {
        this.path = file.toPath();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> tasksFromFile = new ArrayList<>();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                tasksFromFile.add(line);
            }
            String[] infAboutTask;
            for (int i = 1; i < tasksFromFile.size(); i++) {
                infAboutTask = tasksFromFile.get(i).split(",");
                if (Integer.parseInt(infAboutTask[0]) >= count) {
                    count = Integer.parseInt(infAboutTask[0]) + 1;
                }
                switch (infAboutTask[1]) {
                    case "TASK":
                        taskManager.restoreTask(new Task(Integer.parseInt(infAboutTask[0]), infAboutTask[2],
                                infAboutTask[4], TaskStatuses.valueOf(infAboutTask[3])));
                        break;
                    case "EPIC":
                        taskManager.restoreEpic(new Epic(Integer.parseInt(infAboutTask[0]), infAboutTask[2],
                                infAboutTask[4]));
                        break;
                    case "SUBTASK":
                        taskManager.restoreSubtask(new Subtask(Integer.parseInt(infAboutTask[5]),
                                Integer.parseInt(infAboutTask[0]), infAboutTask[2], infAboutTask[4],
                                TaskStatuses.valueOf(infAboutTask[3])));
                }
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        return taskManager;
    }

    private void restoreTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    private void restoreEpic(Epic epic) {
        epic.setStatus(checkStatus(epic.getSubtaskIds()));
        epics.put(epic.getTaskId(), epic);
    }

    private void restoreSubtask(Subtask subtask) {
        subtasks.put(subtask.getTaskId(), subtask);
        Epic epic = epics.get(subtask.getIdOfEpic());
        epic.saveSubtaskId(subtask.getTaskId());
        epic.setStatus(checkStatus(epic.getSubtaskIds()));
    }

    public void save() throws ManagerSaveException {
        if (!tasks.isEmpty() || !epics.isEmpty() || !subtasks.isEmpty()) {
            try (FileWriter writer = new FileWriter(String.valueOf(path), StandardCharsets.UTF_8)) {
                writer.write("id,type,name,status,description,epic\n");
                for (Task task : getTasksList()) {
                    String[] records = new String[]{
                            task.getTaskId().toString(), TasksTypes.TASK.toString(), task.getTaskName(),
                            task.getStatus().toString(), task.getTaskDescription()
                    };
                    writer.write(String.join(",", records));
                    writer.write("\n");
                }
                for (Task task : getEpicsList()) {
                    String[] records = new String[]{
                            task.getTaskId().toString(), TasksTypes.EPIC.toString(), task.getTaskName(),
                            task.getStatus().toString(), task.getTaskDescription()
                    };
                    writer.write(String.join(",", records));
                    writer.write("\n");
                }
                for (Subtask task : getSubtasksList()) {
                    String[] records = new String[]{
                            task.getTaskId().toString(), TasksTypes.SUBTASK.toString(), task.getTaskName(),
                            task.getStatus().toString(), task.getTaskDescription(), String.valueOf(task.getIdOfEpic())
                    };
                    writer.write(String.join(",", records));
                    writer.write("\n");
                }
            } catch (IOException exception) {
                throw new ManagerSaveException("Ошибка сохранения файла");
            }
        } else {
            try {
                new FileWriter(String.valueOf(path), StandardCharsets.UTF_8).close();
            } catch (IOException exception) {
                throw new ManagerSaveException("Ошибка сохранения файла");
            }
        }
    }

    @Override
    public int addNewTask(Task task) throws ManagerSaveException {
        super.addNewTask(task);
        save();
        return task.getTaskId();
    }

    @Override
    public int addNewEpic(Epic epic) throws ManagerSaveException {
        super.addNewEpic(epic);
        save();
        return epic.getTaskId();
    }

    @Override
    public int addNewSubtask(Subtask subtask) throws ManagerSaveException {
        super.addNewSubtask(subtask);
        save();
        return subtask.getTaskId();
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) throws ManagerSaveException {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) throws ManagerSaveException {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(Integer id) throws ManagerSaveException {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() throws ManagerSaveException {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() throws ManagerSaveException {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() throws ManagerSaveException {
        super.deleteEpics();
        save();
    }

    public static void main(String[] args) {
        File file = new File("D:\\Test\\workFile.CSV");
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        taskManager.addNewTask(new Task("Задача 2", "Описание задачи 2",
                TaskStatuses.IN_PROGRESS));
    }
}
