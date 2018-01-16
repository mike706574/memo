package fun.mike.memo;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class StaticQueueManager implements QueueManager {
    private Map<String, Queue<String>> messagesByQueue;

    public StaticQueueManager() {
        messagesByQueue = new LinkedHashMap<>();
    }

    public Map<String, List<String>> getMessagesByQueue() {
        Map<String, List<String>> result = new LinkedHashMap<>();

        for (Map.Entry<String, Queue<String>> entry : messagesByQueue.entrySet()) {
            result.put(entry.getKey(), new LinkedList<>(entry.getValue()));
        }

        return result;
    }

    public void sendMessage(String queueName, String message) {
        if (!messagesByQueue.containsKey(queueName)) {
            messagesByQueue.put(queueName, new LinkedList<>());
        }

        messagesByQueue.get(queueName).add(message);
    }

    public void sendMessage(String queueName, String message, String jmsType) {
        System.out.println("TODO: What to do?");

        if (!messagesByQueue.containsKey(queueName)) {
            messagesByQueue.put(queueName, new LinkedList<>());
        }

        messagesByQueue.get(queueName).add(message);
    }

    public List<String> getMessages(String queueName) {
        if (messagesByQueue.containsKey(queueName)) {
            return new LinkedList<>(messagesByQueue.get(queueName));
        }

        return new LinkedList<>();
    }

    public int countMessages(String queueName) {
        return getMessages(queueName).size();
    }

    public void consumeMessages(String queueName) {
        messagesByQueue.remove(queueName);
    }
}
