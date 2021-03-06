package fun.mike.memo.impl.alpha;

import javax.jms.Connection;
import javax.jms.JMSException;

@FunctionalInterface
public interface ConnectionFunction<T> {
    T apply(Connection conn) throws JMSException;
}