package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManageable {
    Node<Task> lastNode;
    List<Task> printHistoryList = new ArrayList<>();
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
            Node<Task> node = linkLast(element);
            if (historyMap.containsKey(id)) {
                removeNode(id);
            }
            historyMap.put(id, node);
        }
    }

    public static class Node<T extends Task> extends Task {

        public Task data;

        public Task getData() {
            return data;
        }

        public Node<Task> next;
        public Node<Task> prev;

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    '}';
        }

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    public Node<Task> linkLast(Task element) {
        Node<Task> node = new Node<>(lastNode, element, null);
        if (lastNode != null) {
            lastNode.next = node;
            int id = lastNode.getData().getId();
            if (historyMap.containsKey(id))
                historyMap.put(id, lastNode);
        }
        lastNode = node;
        return node;
    }

    public List<Task> getTasks() {
        Node<Task> nodeToMatch = null;

        if (!printHistoryList.isEmpty()) {
            printHistoryList.clear();
        }

        while (printHistoryList.size() < historyMap.size()) {
            for (Node<Task> node : historyMap.values()) {
                if (node.prev == null && !printHistoryList.contains(node.getData())) {
                    printHistoryList.add(node.getData());
                    nodeToMatch = node.next;
                }
                if (node == nodeToMatch) {
                    printHistoryList.add(node.getData());
                    nodeToMatch = node.next;
                }
            }
        }
        int firstIndexInPrintArray = printHistoryList.get(0).getId();
        historyMap.get(firstIndexInPrintArray).prev = null;
        return printHistoryList;
    }

    public void removeNode(int id) {
        if (historyMap.containsKey(id)) {
            Node<Task> node = historyMap.get(id);
            Node<Task> prevNode = node.prev;
            Node<Task> nextNode = node.next;
            if (prevNode != null) {
                prevNode.next = nextNode;
                int prevNodeId = prevNode.getData().getId();
                historyMap.put(prevNodeId, prevNode);
                nextNode.prev = prevNode;
                int nextNodeId = nextNode.getData().getId();
                historyMap.put(nextNodeId, nextNode);

            } else {
                nextNode.prev = null;
            }

            historyMap.remove(id);
        }
    }
}

