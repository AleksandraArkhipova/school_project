package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManageable {

    private final LinkedList<Node<Task>> history = new LinkedList<>();
    private final List<Task> printHistoryList = new ArrayList<>();
    private final HashMap<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void add(Task element) {
        if (element != null) {
            int id = element.getId();
            Node<Task> node = linkLast(element);

            if (historyMap.containsKey(id)) {
                remove(id);
            }
            historyMap.put(id, node);
        }
    }

    @Override
    public List<Task> getHistory() {
        printHistoryList.clear();
        getTasks();
        System.out.println(printHistoryList);
        return printHistoryList;
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            Node<Task> node = historyMap.get(id);
            removeNode(node);
        }
    }

    public Node<Task> linkLast(Task task) {

        Node<Task> node;
        if (!history.isEmpty()) {
            node = new Node<>(history.getLast(), task, null);
            history.add(node);

            int nodeIndex = history.indexOf(node);
            Node<Task> exLast = history.get(nodeIndex - 1);
            exLast.next = node;

        } else {
            node = new Node<>(null, task, null);
            history.add(node);
        }
        return node;
    }

    public void removeNode(Node<Task> node) {

        Node<Task> prev = node.prev;
        Node<Task> next = node.next;
        if (prev != null && next != null) {
            prev.next = next;
            next.prev = prev;
        }
        history.remove(node);

    }

    public void getTasks() {
        for (Node<Task> taskNode : history) {
            Task task = taskNode.getData();
            printHistoryList.add(task);
        }

    }

    public static class Node<Task> {

        public Task data;
        public Node<Task> next;
        public Node<Task> prev;

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        public Task getData() {
            return data;
        }
    }


}
