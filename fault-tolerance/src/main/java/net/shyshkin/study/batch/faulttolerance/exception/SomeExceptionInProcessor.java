package net.shyshkin.study.batch.faulttolerance.exception;

public class SomeExceptionInProcessor extends RuntimeException{
    public SomeExceptionInProcessor(String message) {
        super(message);
    }
}
