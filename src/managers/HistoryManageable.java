package managers;

import tasks.Task;
import java.util.List;

public interface HistoryManageable {

    void add(Task element);

    List<Task> getHistory();

    void remove(int id);
}
