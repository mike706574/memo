package fun.mike.memo;

import java.io.Closeable;
import java.util.List;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.Session;

import fun.mike.memo.impl.JmsMessageParser;
import fun.mike.memo.impl.JmsMessaging;

public class SingleSessionQueueManager implements QueueManager, Closeable, AutoCloseable {
    private Session session;
    private Connection connection;

    public SingleSessionQueueManager(Session session, Connection connection) {
        this.session = session;
        this.connection = connection;
    }

    public void sendMessage(String queueName, String message) {
        JmsMessaging.sendMessage(session, queueName, message);
    }

    public void sendMessage(String queueName, String message, String jmsType) {
        JmsMessaging.sendMessage(session, queueName, message, jmsType);
    }

    public List<String> getMessages(String queueName) {
        return JmsMessaging.getMessages(session, queueName);
    }

    public int countMessages(String queueName) {
        return JmsMessaging.countMessages(session, queueName);
    }

    public void consumeMessages(String queueName) {
        JmsMessaging.consumeMessages(connection, session, queueName);
    }

    public String parseMessage(Message message) {
        return JmsMessageParser.parseToString(message);
    }

    public void close() {
        JmsMessaging.safelyCloseConnAndSession(connection, session);
    }
}
