package net.shyshkin.study.batch.faulttolerance.exception;

public class RetryableException extends RuntimeException{
    public RetryableException(String message) {
        super(message);
    }
}
