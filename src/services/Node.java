package services;

import tasks.Task;

public class Node<T extends Task> {
    public Node<T> previousTask;
    public T currentTask;
    public Node<T> nextTask;

    public Node(Node<T> previousTask, T currentTask, Node<T> nextTask) {
        this.currentTask = currentTask;
        this.previousTask = previousTask;
        this.nextTask = nextTask;
    }
}
