package dk.martinersej.pint.exception.pool;

public class PoolContainsGameException extends IllegalArgumentException {

    public PoolContainsGameException(String message) {
        super(message);
    }

    public PoolContainsGameException(String message, Throwable cause) {
        super(message, cause);
    }
}
