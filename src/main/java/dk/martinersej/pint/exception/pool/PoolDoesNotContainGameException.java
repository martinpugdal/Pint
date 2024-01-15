package dk.martinersej.pint.exception.pool;

public class PoolDoesNotContainGameException extends IllegalArgumentException {

    public PoolDoesNotContainGameException(String message) {
        super(message);
    }

    public PoolDoesNotContainGameException(String message, Throwable cause) {
        super(message, cause);
    }
}
