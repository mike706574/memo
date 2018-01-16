package fun.mike.memo;

import java.util.List;
import javax.jms.ConnectionFactory;
import javax.jms.Message;

import fun.mike.memo.impl.Connector;
import fun.mike.memo.impl.JmsMessageParser;
import fun.mike.memo.impl.JmsMessaging;
import fun.mike.memo.impl.SessionConsumer;
import fun.mike.memo.impl.SessionFunction;

public class MultiSessionQueueManager implements QueueManager {
    private Connector connector;

    public MultiSessionQueueManager(ConnectionFactory connFactory, String username, String password) {
        this.connector = new Connector(connFactory, username, password);
    }

    public void sendMessage(String queueName, String message) {
        withSession(session -> JmsMessaging.sendMessage(session, queueName, message));
    }

    public void sendMessage(String queueName, String message, String jmsType) {
        withSession(session -> JmsMessaging.sendMessage(session, queueName, message, jmsType));
    }

    public List<String> getMessages(String queueName) {
        return viaSession(session -> JmsMessaging.getMessages(session, queueName));
    }

    public int countMessages(String queueName) {
        return viaSession(session -> JmsMessaging.countMessages(session, queueName));
    }

    public void consumeMessages(String queueName) {
        JmsMessaging.withConnAndSession(connector, (conn, session) -> JmsMessaging.consumeMessages(conn, session, queueName));
    }

    public String parseMessage(Message message) {
        return JmsMessageParser.parseToString(message);
    }

    private void withSession(SessionConsumer consumer) {
        JmsMessaging.withSession(connector, consumer);
    }

    private <T> T viaSession(SessionFunction<T> function) {
        return JmsMessaging.viaSession(connector, function);
    }

}
