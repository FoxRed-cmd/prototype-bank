package neo.study.calculator.utils.exception;

import java.util.List;

public class NotValidException extends RuntimeException {
    private List<String> exceptions;

    public NotValidException(List<String> exceptions, String message) {
        super(message);
        this.exceptions = exceptions;
    }

    public List<String> getExceptions() {
        return exceptions;
    }
}
