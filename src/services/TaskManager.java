package services;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

class TaskManager {
    HashMap<TypesOfTasks, HashMap<Integer, Object>> tasksStorage;
    private int count;

    TaskManager() {
        count = 0;
        tasksStorage = new HashMap<>();
        HashMap<Integer, Object> tasks = new HashMap<>();
        HashMap<Integer, Object> epics = new HashMap<>();
        HashMap<Integer, Object> subtasks = new HashMap<>();
        tasksStorage.put(TypesOfTasks.TASK, tasks);
        tasksStorage.put(TypesOfTasks.EPIC, epics);
        tasksStorage.put(TypesOfTasks.SUBTASK, subtasks);

    }

    int setID() {
        return count++;
    }

    ArrayList<Object> getAllTasks() {
        ArrayList<Object> allTasks = new ArrayList<>();
        for (HashMap<Integer, Object> tasks : tasksStorage.values()) {
            allTasks.addAll(tasks.values());
        }
        return allTasks;
    }

    HashMap<TypesOfTasks, HashMap<Integer, Object>> deleteAllTasks() {
        for (TypesOfTasks type : tasksStorage.keySet()) {
            tasksStorage.get(type).clear();
        }
        return tasksStorage;
    }

    Object findTaskById(int idNumber) {
        Object result = null;
        for (HashMap<Integer, Object> tasks : tasksStorage.values()) {
            for (int num : tasks.keySet()) {
                if (num == idNumber) {
                    result = tasks.get(idNumber);
                    break;
                }
            }
        }
        return result;
    }

    ArrayList<SubTask> findSubtasksOfEpic(int epicId) {
        ArrayList<SubTask> subtasksOfEpic = new ArrayList<>();
        for (Object object : tasksStorage.get(TypesOfTasks.SUBTASK).values()) {
            if (((SubTask) object).getIdOfEpic() == epicId) {
                SubTask task = (SubTask) object;
                subtasksOfEpic.add(task);
            }
        }
        return subtasksOfEpic;
    }

    void deleteSubtasksOfEpic(int epicId) {
        HashMap<Integer, Object> subtasks = tasksStorage.get(TypesOfTasks.SUBTASK);
        ArrayList<Integer> numberOfTasks = new ArrayList<>();
        for (int i : subtasks.keySet()) {
            SubTask task = (SubTask) subtasks.get(i);
            if (task.getIdOfEpic() == epicId) {
                numberOfTasks.add(i);
            }
        }
        for (Integer numberOfTask : numberOfTasks) {
            subtasks.remove(numberOfTask);
        }
        numberOfTasks.clear();
    }

    HashMap<TypesOfTasks, HashMap<Integer, Object>> deleteTaskById(int idNumber) {
        if (tasksStorage.get(TypesOfTasks.EPIC).containsKey(idNumber)) {
            deleteSubtasksOfEpic(idNumber);
        }
        for (HashMap<Integer, Object> tasks : tasksStorage.values()) {
            for (int num : tasks.keySet()) {
                if (num == idNumber) {
                    tasks.remove(idNumber);
                    break;
                }
            }
        }
        return tasksStorage;
    }

    HashMap<TypesOfTasks, HashMap<Integer, Object>> createTask(Task task) {
        if (task != null) {
            tasksStorage.get(TypesOfTasks.TASK).put(task.getTaskId(), task);
        }
        return tasksStorage;
    }

    HashMap<TypesOfTasks, HashMap<Integer, Object>> createEpic(Epic epic) {
        if (epic != null) {
            epic.setStatus(epic.checkStatus(findSubtasksOfEpic(epic.getTaskId())));
            tasksStorage.get(TypesOfTasks.EPIC).put(epic.getTaskId(), epic);
        }
        return tasksStorage;
    }

    HashMap<TypesOfTasks, HashMap<Integer, Object>> createSubtask(SubTask subtask) {
        if (subtask != null && tasksStorage.get(TypesOfTasks.EPIC).containsKey(subtask.getIdOfEpic())) {
            tasksStorage.get(TypesOfTasks.SUBTASK).put(subtask.getTaskId(), subtask);
        }
        return tasksStorage;
    }

    HashMap<TypesOfTasks, HashMap<Integer, Object>> updateTask(Task task) {
        if (task != null && tasksStorage.get(TypesOfTasks.TASK).containsKey(task.getTaskId())) {
            tasksStorage.get(TypesOfTasks.TASK).put(task.getTaskId(), task);
        }
        return tasksStorage;
    }

    HashMap<TypesOfTasks, HashMap<Integer, Object>> updateEpic(Epic epic) {
        if (epic != null && tasksStorage.get(TypesOfTasks.EPIC).containsKey(epic.getTaskId())) {
            epic.setStatus(epic.checkStatus(findSubtasksOfEpic(epic.getTaskId())));
            tasksStorage.get(TypesOfTasks.EPIC).put(epic.getTaskId(), epic);
        }
        return tasksStorage;
    }

    HashMap<TypesOfTasks, HashMap<Integer, Object>> updateSubtask(SubTask subtask) {
        if (subtask != null && tasksStorage.get(TypesOfTasks.SUBTASK).containsKey(subtask.getTaskId())) {
            tasksStorage.get(TypesOfTasks.SUBTASK).put(subtask.getTaskId(), subtask);
            Epic epic = (Epic) tasksStorage.get(TypesOfTasks.EPIC).get(subtask.getIdOfEpic());
            epic.setStatus(epic.checkStatus(findSubtasksOfEpic(epic.getTaskId())));
        }
        return tasksStorage;
    }
}