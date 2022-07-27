package main;

import java.util.Scanner;
import managers.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

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

        SubTask subtask3 = new SubTask(
                "Сесть в самолёт",
                "Переноску можно поставить на колени",
                TaskStatuses.NEW,
                taskManager.generateId(),
                epic1.getId());

        Epic epic2 = new Epic(
                "Подготовить велосипед к сезону",
                "Перед выходными",
                taskManager.generateId());

        Scanner scanner = new Scanner(System.in); 
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

                taskManager.getTaskById(task2.getId());//(ID2)
                taskManager.getSubTaskById(subtask3.getId());//(ID6)
                taskManager.getEpicById(epic2.getId());//(ID7)

                taskManager.getHistory();

                taskManager.getEpicById(epic1.getId()); //(ID3)
                taskManager.getEpicById(epic2.getId()); //(ID7)
                taskManager.getSubTaskById(subtask3.getId());//(ID6)
                taskManager.getSubTaskById(subtask1.getId());//(ID4)

                taskManager.getHistory();

                taskManager.getEpicById(epic1.getId()); //(ID3)
                taskManager.getSubTaskById(subtask1.getId());//(ID4)
                taskManager.getSubTaskById(subtask2.getId());//(ID5)
                taskManager.getSubTaskById(subtask3.getId());//(ID6)
                taskManager.getTaskById(task2.getId()); //(ID2)
                taskManager.getTaskById(task1.getId()); //(ID1)

                taskManager.getHistory();


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

                taskManager.removeTaskById(task1.getId()); //(ID1)
                taskManager.removeSubTaskById(subtask1.getId()); //(ID4)
                taskManager.removeEpicById(epic1.getId()); //(ID3)

            } else if (userInput == 7) {

                taskManager.getSubtasksByEpicId(epic1.getId());

            }
            printMenu();
        }
    }

    public static void printMenu() {

        System.out.println("\n1. Добавление всех задач \n");
        System.out.println("2. Получение списка всех задач\n");
        System.out.println("3. Удаление всех задач \n");
        System.out.println("4. Просмотр истории просмотров без повторов \n");
        System.out.println("5. Обновление выбранных элементов \n");
        System.out.println("6. Удаление выбранных элементов\n");
        System.out.println("7. Получение списка всех подзадач эпика № 1 \n");

    }
}
