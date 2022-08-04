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
        Node<Task> nodeToAdd;

        for (int i = 1; i < historyMap.size(); i++) {
            if (!printHistoryList.contains(nodeForCycle.getData())) {
                nodeToAdd = nodeForCycle;
                printHistoryList.add(nodeToAdd.getData());
            } else if (nodeForCycle.next != null) {
                    nodeToAdd = nodeForCycle.next;
                printHistoryList.add(nodeToAdd.getData());
            } else {
                nodeToAdd = null;
            }

            if (nodeToAdd.next != null) {
                nodeForCycle = nodeToAdd.next;
            }
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
                int prevNodeId = prevNode.getData().getId();
                historyMap.put(prevNodeId, prevNode);

            }

            if (nextNode != null) {
                if (prevNode == null) {
                    firstNode = nextNode;
                    firstNode.prev = null;
                    historyMap.put(firstNode.getData().getId(), firstNode);
                } else {
                    nextNode.prev = prevNode;
                }
            }
            historyMap.remove(id);
        }
    }
}

