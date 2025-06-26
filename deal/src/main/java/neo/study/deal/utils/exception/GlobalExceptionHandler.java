package neo.study.deal.utils.exception;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleLoanRejection(HttpClientErrorException ex) {
        Matcher matcher = Pattern.compile("\\[[^\\]]*\\]").matcher(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(matcher.find() ? matcher.group() : ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNoSuchElementException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
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
