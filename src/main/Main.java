package main;

import java.util.Scanner;
import managers.*;
import tasks_and_epics.Epic;
import tasks_and_epics.SubTask;
import tasks_and_epics.Task;

@SuppressWarnings("InfiniteLoopStatement")

public class Main {

    public static void main(String[] args) {

    TaskManageable taskManager = Managers.getDefault();

        Task task1 = new Task(
                "Забрать посылку с почты",
                "До 15.06.22",
                TaskStatuses.NEW,
                taskManager.generateId());

        Task task2 = new Task(
                "Пропылесосить квартиру",
                "До 13.06.22",
                TaskStatuses.NEW,
                taskManager.generateId());

        Epic epic1 = new Epic(
                "Получить полётный сертификат для кота",
                "Действует 3 дня",
                taskManager.generateId());

        SubTask subtask1 = new SubTask(
                "Сделать прививку от бешенства",
                "Не забыть про штамп!",
                TaskStatuses.NEW,
                taskManager.generateId(),
                epic1.getId());

        SubTask subtask2 = new SubTask(
                "Пройти ветконтроль в аэропорту",
                "Приехать за 3 часа до вылета",
                TaskStatuses.DONE,
                taskManager.generateId(),
                epic1.getId());

        Epic epic2 = new Epic(
                "Подготовить велосипед к сезону",
                "Перед выходными",
                taskManager.generateId());

        SubTask subtask3 = new SubTask(
                "Подкачать шины",
                "Насос в гараже",
                TaskStatuses.NEW,
                taskManager.generateId(),
                epic2.getId());

        Scanner scanner = new Scanner(System.in); // сканнер и меню сделала для удобства отладки
        int userInput;

        System.out.println("Добро пожаловать в менеджер задач. Выберите действие: \n");
        printMenu();
        while (true) {
            userInput = scanner.nextInt();
            if (userInput == 1) {

                taskManager.addTask(task1);
                taskManager.addTask(task2);
                taskManager.addEpic(epic1);
                taskManager.addEpic(epic2);
                taskManager.addSubTask(subtask1);
                taskManager.addSubTask(subtask2);
                taskManager.addSubTask(subtask3);

            } else if (userInput == 2) {

                taskManager.getListOfAllTasks();
                taskManager.getListOfAllSubTasks();
                taskManager.getListOfAllEpics();

            } else if (userInput == 3) {

                taskManager.removeAllTasks();
                taskManager.removeAllSubTasks();
                taskManager.removeAllEpics();

            } else if (userInput == 4) {

                taskManager.getTaskById(task2.getId());
                taskManager.getSubTaskById(subtask3.getId());
                taskManager.getEpicById(epic2.getId());

                System.out.println(taskManager); // распечатка истории, 3 элемента

                taskManager.getEpicById(epic1.getId());
                taskManager.getSubTaskById(subtask1.getId());

                taskManager.getHistory(); // получение истории через метод, 5 элементов

                taskManager.getEpicById(epic1.getId());
                taskManager.getSubTaskById(subtask1.getId());
                taskManager.getTaskById(task2.getId());
                taskManager.getSubTaskById(subtask3.getId());
                taskManager.getTaskById(task2.getId());
                taskManager.getSubTaskById(subtask3.getId());

                taskManager.getHistory();// получение истории через метод, 10 элементов

            } else if (userInput == 5) {

                task1 = new Task(
                        "Забрать посылку с почты",
                        "До 20.06.22",
                        TaskStatuses.DONE,
                        task1.getId());

                epic1 = new Epic(
                        "Получить ветеринарный сертификат для кота",
                        "До конца августа",
                        epic1.getId());

                subtask1 = new SubTask(
                        "Сделать прививку от бешенства",
                        "Не забыть про штамп!",
                        TaskStatuses.NEW,
                        subtask1.getId(),
                        subtask1.getEpicId());

                taskManager.updateTask(task1);
                taskManager.updateSubTask(subtask1);
                taskManager.updateEpic(epic1);

            } else if (userInput == 6) {

                taskManager.removeTaskById(task1.getId());
                taskManager.removeSubTaskById(subtask1.getId());
                taskManager.removeEpicById(epic1.getId());

            } else if (userInput == 7) {

                taskManager.getSubtasksByEpicId(epic1.getId());

            }
            printMenu();
        }
    }

    public static void printMenu() {

        System.out.println("\n1. Добавить все задачи, подзадачи, эпики\n");
        System.out.println("2. Получить список всех задач, подзадач, эпиков\n");
        System.out.println("3. Удалить все задачи, подзадачи, эпики \n");
        System.out.println("4. Получить задачу № 2, подзадачу № 3 и эпик № 2 по идентификатору\n");
        System.out.println("5. Обновить задачу, подзадачу и эпик под номером 1\n");
        System.out.println("6. Удалить задачу, подзадачу и эпик под номером 1 по идентификатору\n");
        System.out.println("7. Получить список всех подзадач эпика номер 1 \n");

    }
}
