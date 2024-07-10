package services;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
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
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
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
                TaskStatuses.valueOf(infAboutTask[3]));
        tasks.put(task.getTaskId(), task);
    }

    private void restoreEpic(String[] infAboutTask) {
        Epic epic = new Epic(Integer.parseInt(infAboutTask[0]), infAboutTask[2], infAboutTask[4]);
        epic.setStatus(TaskStatuses.valueOf(infAboutTask[3]));
        epics.put(epic.getTaskId(), epic);
    }

    private void restoreSubtask(String[] infAboutTask) {
        Subtask subtask = new Subtask(Integer.parseInt(infAboutTask[5]), Integer.parseInt(infAboutTask[0]),
                infAboutTask[2], infAboutTask[4], TaskStatuses.valueOf(infAboutTask[3]));
        subtasks.put(subtask.getTaskId(), subtask);
        Epic epic = epics.get(subtask.getIdOfEpic());
        epic.saveSubtaskId(subtask.getTaskId());
    }

    public void save() throws ManagerSaveException {
        try (FileWriter writer = new FileWriter(String.valueOf(path), StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasksList()) {
                String record = task.toStringFile();
                writer.write(record);
                writer.write("\n");
            }
            for (Task task : getEpicsList()) {
                String record = task.toStringFile();
                writer.write(record);
                writer.write("\n");
            }
            for (Subtask task : getSubtasksList()) {
                String record = task.toStringFile();
                writer.write(record);
                writer.write("\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

    @Override
    public int addNewTask(Task task) {
        super.addNewTask(task);
        save();
        return task.getTaskId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
        return epic.getTaskId();
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
        return subtask.getTaskId();
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

    public static void main(String[] args) {
        File file = new File("src\\workFile.CSV");
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        taskManager.deleteTasks();
    }
}
