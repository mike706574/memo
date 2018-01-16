package fun.mike.memo.impl;

import javax.jms.JMSException;
import javax.jms.Session;

@FunctionalInterface
public interface SessionFunction<T> {
    T apply(Session session) throws JMSException;
}