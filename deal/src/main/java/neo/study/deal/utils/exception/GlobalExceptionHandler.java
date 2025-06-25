package neo.study.deal.utils.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleLoanRejection(HttpClientErrorException ex) {
        Matcher matcher = Pattern.compile("\\[[^\\]]*\\]").matcher(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(matcher.find() ? matcher.group() : ex.getMessage());
    }
}
