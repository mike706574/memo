package fun.mike.memo.impl.alpha;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

@FunctionalInterface
public interface ConnectionAndSessionConsumer {
    void accept(Connection conn, Session session) throws JMSException;
}