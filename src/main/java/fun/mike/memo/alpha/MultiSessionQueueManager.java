package fun.mike.memo.alpha;

import java.util.List;
import javax.jms.ConnectionFactory;
import javax.jms.Message;

import fun.mike.memo.impl.alpha.Connector;
import fun.mike.memo.impl.alpha.JmsMessageParser;
import fun.mike.memo.impl.alpha.JmsMessaging;
import fun.mike.memo.impl.alpha.SessionConsumer;
import fun.mike.memo.impl.alpha.SessionFunction;

public class MultiSessionQueueManager implements QueueManager {
    private Connector connector;

    public MultiSessionQueueManager(ConnectionFactory connFactory, String username, String password) {
        this.connector = new Connector(connFactory, username, password);
    }

    public void sendMessage(String queueName, String message) {
        useSession(session -> JmsMessaging.sendMessage(session, queueName, message));
    }

    public void sendMessage(String queueName, String message, String jmsType) {
        useSession(session -> JmsMessaging.sendMessage(session, queueName, message, jmsType));
    }

    public List<String> getMessages(String queueName) {
        return withSession(session -> JmsMessaging.getMessages(session, queueName));
    }

    public int countMessages(String queueName) {
        return withSession(session -> JmsMessaging.countMessages(session, queueName));
    }

    public List<String> consumeMessages(String queueName) {
        return JmsMessaging.withConnAndSession(connector, (conn, session) -> JmsMessaging.consumeMessages(conn, session, queueName));
    }

    public String parseMessage(Message message) {
        return JmsMessageParser.parseToString(message);
    }

    private void useSession(SessionConsumer consumer) {
        JmsMessaging.useSession(connector, consumer);
    }

    private <T> T withSession(SessionFunction<T> function) {
        return JmsMessaging.withSession(connector, function);
    }

}
