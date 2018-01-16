package fun.mike.memo.alpha;

public class QueueManagerException extends RuntimeException {
    public QueueManagerException(String message) {
        super(message);
    }

    public QueueManagerException(Throwable cause) {
        super(cause);
    }

    public QueueManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
