package managers;

import tasks_and_epics.Task;
import java.util.List;

public interface HistoryManageable {

    void add(Task element);

    List<Task> getHistory();
}
