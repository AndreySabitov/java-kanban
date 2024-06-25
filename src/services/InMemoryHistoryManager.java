package services;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> searchHistoryList = new ArrayList<>();
    private final Map<Integer, Node<Task>> linkedHistoryList = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(searchHistoryList);
    }

    private void getTasks() {
        searchHistoryList.clear();
        if (head != null) {
            searchHistoryList.add(head.currentTask);
            Node<Task> nextNode = head.nextTask;
            while (nextNode != null) {
                searchHistoryList.add(nextNode.currentTask);
                nextNode = nextNode.nextTask;
            }
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getTaskId());
            linkLast(task);
            getTasks();
        }
    }

    private void linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.nextTask = newNode;
        }
        linkedHistoryList.put(task.getTaskId(), newNode);
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prevNode = node.previousTask;
        Node<Task> nextNode = node.nextTask;
        if (prevNode == null && nextNode != null) {
            nextNode.previousTask = null;
            head = nextNode;
        }
        if (nextNode == null && prevNode != null) {
            prevNode.nextTask = null;
            tail = prevNode;
        }
        if (nextNode == null && prevNode == null) {
            head = null;
            tail = null;
        }
        if (nextNode != null && prevNode != null) {
            prevNode.nextTask = nextNode;
            nextNode.previousTask = prevNode;
        }
        linkedHistoryList.remove(node.currentTask.getTaskId());
    }

    @Override
    public void remove(int id) {
        if (linkedHistoryList.containsKey(id)) {
            removeNode(linkedHistoryList.get(id));
            getTasks();
        }
    }
}
