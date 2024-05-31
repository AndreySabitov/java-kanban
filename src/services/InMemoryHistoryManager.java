package services;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> searchHistoryList = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return searchHistoryList;
    }

    @Override
    public void add(Task task) {
        if (searchHistoryList.size() == 10) {
            searchHistoryList.removeFirst();
        }
        searchHistoryList.add(task);
    }
}
