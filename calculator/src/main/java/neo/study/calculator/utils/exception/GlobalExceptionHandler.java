package neo.study.calculator.utils.exception;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoanRejectionException.class)
    public ResponseEntity<String> handleLoanRejection(LoanRejectionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(NotValidException.class)
    public ResponseEntity<List<String>> handleValidationExceptions(NotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getExceptions());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException formatEx) {
            Class<?> targetType = formatEx.getTargetType();
            if (targetType.isEnum()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "Invalid value for enum " + targetType.getSimpleName(),
                                "allowedValues", targetType.getEnumConstants()));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Malformed JSON request"));
    }
}
