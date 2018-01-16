package fun.mike.memo.impl.alpha;

import javax.jms.Connection;
import javax.jms.JMSException;

@FunctionalInterface
public interface ConnectionConsumer {
    void accept(Connection conn) throws JMSException;
}