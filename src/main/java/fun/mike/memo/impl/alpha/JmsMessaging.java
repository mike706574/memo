package fun.mike.memo.impl.alpha;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import fun.mike.memo.alpha.QueueManagerException;

public class JmsMessaging {
    public static List<Message> getJmsMessages(Session session, String queueName) {
        try {
            Queue queue = session.createQueue(queueName);
            QueueBrowser browser = session.createBrowser(queue);
            Enumeration enumeration = browser.getEnumeration();

            List<Message> messages = new LinkedList<>();
            while (enumeration.hasMoreElements()) {
                Message message = (Message) enumeration.nextElement();
                messages.add(message);
            }
            return messages;
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        }
    }

    public static List<String> getMessages(Session session, String queueName) {
        try {
            Queue queue = session.createQueue(queueName);
            QueueBrowser browser = session.createBrowser(queue);
            Enumeration enumeration = browser.getEnumeration();

            List<String> messages = new LinkedList<>();
            while (enumeration.hasMoreElements()) {
                Message message = (Message) enumeration.nextElement();
                messages.add(JmsMessageParser.parseToString(message));
            }
            return messages;
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        }
    }

    public static int countMessages(Session session, String queueName) {
        try {
            Queue queue = session.createQueue(queueName);
            QueueBrowser browser = session.createBrowser(queue);
            Enumeration enumeration = browser.getEnumeration();

            int count = 0;
            while (enumeration.hasMoreElements()) {
                count++;
                enumeration.nextElement();
            }
            return count;
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        }
    }

    public static void sendMessage(Session session, String queueName, String message) {
        try {
            Queue queue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(queue);
            TextMessage textMessage = session.createTextMessage(message);
            producer.send(session.createTextMessage(message));
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        }
    }

    public static void sendMessage(Session session, String queueName, String message, String jmsType) {
        try {
            Queue queue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(queue);
            TextMessage textMessage = session.createTextMessage(message);
            textMessage.setJMSType(jmsType);
            producer.send(textMessage);
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        }
    }

    public static void sendMessage(Session session, String queueName, String message, Map<String, Object> props) {
        System.out.println("Props: " + props);
        try {
            Queue queue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(queue);
            TextMessage textMessage = session.createTextMessage(message);

            if (props.containsKey("Type")) {
                String jmsType = (String) props.get("Type");
                System.out.println("Setting JMS type to " + jmsType + ".");
                textMessage.setJMSType(jmsType);
            }

            for (Map.Entry<String, Object> entry : props.entrySet()) {
                textMessage.setObjectProperty(entry.getKey(), entry.getValue());
            }

            producer.send(textMessage);
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        }
    }

    public static List<String> consumeMessages(Connection conn, Session session, String queueName) {
        try {
            List<String> messages = new LinkedList<>();

            Queue queue = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(queue);
            Message message;
            boolean end = false;

            while (!end) {
                conn.start();
                message = consumer.receive(1000);
                if (message == null) {
                    end = true;
                } else {
                    messages.add(JmsMessageParser.parseToString(message));
                }
            }

            return messages;
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        }
    }

    public static void safelyCloseConnAndSession(Connection connection, Session session) {
        safelyCloseSession(session);
        safelyCloseConnection(connection);
    }

    public static void safelyCloseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException ex) {
                throw new RuntimeException("Failed to close JMS connection.", ex);
            }
        }
    }

    public static void safelyCloseSession(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException ex) {
                throw new RuntimeException("Failed to close JMS session.", ex);
            }
        }
    }

    public static void useConnAndSession(Connector connector,
            ConnectionAndSessionConsumer consumer) {
        Connection conn = null;
        Session session = null;

        try {
            conn = connector.getConnection();
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer.accept(conn, session);
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        } finally {
            JmsMessaging.safelyCloseConnAndSession(conn, session);
        }
    }

    public static <T> T withConnAndSession(Connector connector,
            ConnectionAndSessionFunction<T> consumer) {
        Connection conn = null;
        Session session = null;

        try {
            conn = connector.getConnection();
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            return consumer.apply(conn, session);
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        } finally {
            JmsMessaging.safelyCloseConnAndSession(conn, session);
        }
    }

    public static void useSession(Connector connector, SessionConsumer consumer) {
        Connection conn = null;
        Session session = null;

        try {
            conn = connector.getConnection();
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer.accept(session);
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        } finally {
            JmsMessaging.safelyCloseConnAndSession(conn, session);
        }
    }

    public static <T> T withSession(Connector connector,
            SessionFunction<T> function) {
        Connection conn = null;
        Session session = null;

        try {
            conn = connector.getConnection();
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            return function.apply(session);
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        } finally {
            JmsMessaging.safelyCloseConnAndSession(conn, session);
        }
    }

    private static <T> T wrapJmsException(JmsExceptionThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        }
    }

    @FunctionalInterface
    private interface JmsExceptionThrowingSupplier<T> {
        T get() throws JMSException;
    }
}
