package edu.ivanuil.friendalertbot.exception;

public class ClickHouseClientException extends RuntimeException {

    public ClickHouseClientException() {
        super();
    }

    public ClickHouseClientException(String message) {
        super(message);
    }

    public ClickHouseClientException(Throwable cause) {
        super(cause);
    }

}
