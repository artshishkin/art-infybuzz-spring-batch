package net.shyshkin.study.batch.faulttolerance.exception;

public class SomeExceptionInWriter extends RuntimeException{
    public SomeExceptionInWriter(String message) {
        super(message);
    }
}
