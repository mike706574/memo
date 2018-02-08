package fun.mike.memo.impl.alpha;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

@FunctionalInterface
public interface ConnectionAndSessionFunction<T> {
    T apply(Connection conn, Session session) throws JMSException;
}