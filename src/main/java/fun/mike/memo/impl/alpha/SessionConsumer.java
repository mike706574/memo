package fun.mike.memo.impl.alpha;

import javax.jms.JMSException;
import javax.jms.Session;

@FunctionalInterface
public interface SessionConsumer {
    void accept(Session session) throws JMSException;
}
