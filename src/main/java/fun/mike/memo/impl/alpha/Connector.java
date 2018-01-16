package fun.mike.memo.impl.alpha;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import fun.mike.memo.alpha.QueueManagerException;

public class Connector {
    private ConnectionFactory connFactory;
    private String username;
    private String password;

    public Connector(ConnectionFactory connFactory) {
        this.connFactory = connFactory;
    }

    public Connector(ConnectionFactory connFactory, String username, String password) {
        this.connFactory = connFactory;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() {
        try {
            if (username == null) {
                return connFactory.createConnection();
            }

            return connFactory.createConnection(username, password);
        } catch (JMSException ex) {
            throw new QueueManagerException(ex);
        }
    }
}
