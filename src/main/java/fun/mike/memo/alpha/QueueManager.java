package fun.mike.memo.alpha;

import java.util.List;

public interface QueueManager {
    void sendMessage(String queueName, String message);

    void sendMessage(String queueName, String message, String jmsType);

    List<String> getMessages(String queueName);

    int countMessages(String queueName);

    void consumeMessages(String queueName);
}
