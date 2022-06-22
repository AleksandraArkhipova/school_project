package managers;

import tasks_and_epics.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManageable {

    static private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task element) {
        if (history.size() < 10) {
            history.add(element);
        } else {
            history.remove(0);
            history.add(element);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager {"
                + " history.size = "
                + history.size()
                + ", history.contains: "
                + history +
                '}';
    }
}
