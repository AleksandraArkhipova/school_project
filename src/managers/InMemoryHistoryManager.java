package managers;

import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManageable {

    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task element) {
        if (history.size() >= 10) {
            history.remove(0);
        }
            history.add(element);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
