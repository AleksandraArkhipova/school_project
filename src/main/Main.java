package main;

import java.util.Scanner;

import manager_and_tasks.Epic;
import manager_and_tasks.Manager;
import manager_and_tasks.Task;
import manager_and_tasks.SubTask;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        Task task1 = new Task(
                "Забрать посылку с почты",
                "До 15.06.22",
                "NEW",
                manager.generateId());

        Task task2 = new Task(
                "Пропылесосить квартиру",
                "До 13.06.22",
                "NEW",
                manager.generateId());

        Epic epic1 = new Epic(
                "Получить полётный сертификат для кота",
                "Действует 3 дня",
                manager.generateId());

        SubTask subtask1 = new SubTask(
                "Сделать прививку от бешенства",
                "Не забыть про штамп!",
                "NEW",
                manager.generateId(),
                epic1.getId());

        SubTask subtask2 = new SubTask(
                "Пройти ветконтроль в аэропорту",
                "Приехать за 3 часа до вылета",
                "DONE",
                manager.generateId(),
                epic1.getId());

        Epic epic2 = new Epic(
                "Подготовить велосипед к сезону",
                "Перед выходными",
                manager.generateId());

        SubTask subtask3 = new SubTask(
                "Подкачать шины",
                "Насос в гараже",
                "NEW",
                manager.generateId(),
                epic2.getId());

        Scanner scanner = new Scanner(System.in); // сканнер и меню сделала для удобства отладки
        int userInput;

        System.out.println("Добро пожаловать в менеджер задач. Выберите действие: \n");
        printMenu();
        while (true) {
            userInput = scanner.nextInt();
            if (userInput == 1) {

                manager.addTask(task1);
                manager.addTask(task2);
                manager.addEpic(epic1);
                manager.addEpic(epic2);
                manager.addSubTask(subtask1);
                manager.addSubTask(subtask2);
                manager.addSubTask(subtask3);

            } else if (userInput == 2) {

                manager.getListOfAllTasks();
                manager.getListOfAllSubTasks();
                manager.getListOfAllEpics();

            } else if (userInput == 3) {

                manager.removeAllTasks();
                manager.removeAllSubTasks();
                manager.removeAllEpics();

            } else if (userInput == 4) {

                manager.getTaskById(task2.getId());
                manager.getSubTaskById(subtask3.getId());
                manager.getEpicById(epic2.getId());

            } else if (userInput == 5) {

                task1 = new Task(
                        "Забрать посылку с почты",
                        "До 20.06.22",
                        "DONE",
                        task1.getId());

                epic1 = new Epic(
                        "Получить ветеринарный сертификат для кота",
                        "До конца августа",
                        epic1.getId());

                subtask1 = new SubTask(
                        "Сделать прививку от бешенства",
                        "Не забыть про штамп!",
                        "NEW",
                        subtask1.getId(),
                        subtask1.getEpicId());

                manager.updateTask(task1);
                manager.updateSubTask(subtask1);
                manager.updateEpic(epic1);

            } else if (userInput == 6) {

                manager.removeTaskById(task1.getId());
                manager.removeSubTaskById(subtask1.getId());
                manager.removeEpicById(epic1.getId());

            } else if (userInput == 7) {

                manager.getSubtasksByEpicId(epic1.getId());

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
