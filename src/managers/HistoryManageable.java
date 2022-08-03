package managers;

import tasks.Task;
import java.util.List;

public interface HistoryManageable {

    List<Task> getHistoryList();

    void remove(int id);

    void add(Task element);
}
