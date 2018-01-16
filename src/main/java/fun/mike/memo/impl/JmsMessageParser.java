package fun.mike.memo.impl;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import fun.mike.memo.QueueManagerException;

public class JmsMessageParser {
    public static String parseToString(Message message) {
        try {
            if (message instanceof TextMessage) {
                return ((TextMessage) message).getText();
            }

            if (message instanceof BytesMessage) {
                byte[] body = new byte[(int) ((BytesMessage) message).getBodyLength()];
                ((BytesMessage) message).readBytes(body);
                return new String(body);
            }

            System.out.println(message.getClass().getName());
            throw new QueueManagerException("Unable to parse message: " + message);
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        }
    }

}
