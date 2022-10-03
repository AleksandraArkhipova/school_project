package main;

import java.io.File;
import java.io.IOException;
import managers.*;

public class Main {

    public static void main(String[] args) throws IOException {

        TaskManageable taskManager = Managers.getDefault();
        FileBackedTasksManager backedTasksManager =
                FileBackedTasksManager.loadFromFile(new File(
                        "files", "FileBackedTasksManager.csv"));
    }
}
