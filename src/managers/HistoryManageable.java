package managers;

import tasks.Task;
import java.util.List;

public interface HistoryManageable {

    List<Task> getHistoryList();

    List<Task> getPrintHistoryList();

    void setPrintHistoryList(List<Task> printHistoryList);

    void remove(int id);

    void add(Task element);

}
