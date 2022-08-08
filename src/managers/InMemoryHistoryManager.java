package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManageable {
    private Node<Task> lastNode;
    private Node<Task> firstNode;
    private final List<Task> printHistoryList = new ArrayList<>();
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public List<Task> getHistoryList() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public void add(Task element) {
        if (element != null) {
            int id = element.getId();
            if (historyMap.containsKey(id)) {
                removeNode(id);
            }
            Node<Task> node = linkLast(element);
            historyMap.put(id, node);
        }
    }

    public static class Node<T extends Task> extends Task {

        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        public T getData() {
            return data;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    '}';
        }
    }

    public Node<Task> linkLast(Task element) {
        Node<Task> node = new Node<>(lastNode, element, null);
        if (lastNode != null) {
            lastNode.next = node;

        } else {
            firstNode = node;
        }
        lastNode = node;
        return node;
    }

    public List<Task> getTasks() {
        if (!printHistoryList.isEmpty()) {
            printHistoryList.clear();
        }
        Node<Task> nodeForCycle = firstNode;
        printHistoryList.add(firstNode.getData());

        while (nodeForCycle != null) {
            printHistoryList.add(nodeForCycle.getData());
            nodeForCycle = nodeForCycle.next;
        }

        return printHistoryList;
    }

    private void removeNode(int id) {
        if (historyMap.containsKey(id)) {

            Node<Task> node = historyMap.get(id);
            Node<Task> prevNode = node.prev;
            Node<Task> nextNode = node.next;

            if (prevNode != null) {
                prevNode.next = nextNode;
            } else {
                firstNode = nextNode;
            }
            if (nextNode != null) {
                nextNode.prev = prevNode;
            } else {
                lastNode = prevNode;
            }
            historyMap.remove(id);
        }
    }
}

