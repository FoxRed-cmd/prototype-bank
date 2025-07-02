package neo.study.deal.utils.exception;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neo.study.deal.dto.ApplicationStatus;
import neo.study.deal.dto.ChangeType;
import neo.study.deal.service.StatementService;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final StatementService statementService;

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleLoanRejection(HttpClientErrorException ex, HttpServletRequest request) {
        Matcher matcher = Pattern.compile("\\[[^\\]]*\\]").matcher(ex.getMessage());

        String statementId = getStatementIdFromPath(request.getRequestURI());
        updateStatusAfterError(UUID.fromString(statementId));

        return ResponseEntity.status(ex.getStatusCode())
                .body(matcher.find() ? matcher.group() : ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<String> handleResourceAccessException(ResourceAccessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNoSuchElementException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        Throwable cause = ex.getCause();

        String statementId = getStatementIdFromPath(request.getRequestURI());
        updateStatusAfterError(UUID.fromString(statementId));

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

    private void updateStatusAfterError(UUID statementId) {
        try {
            if (statementId != null) {
                var statement = statementService.updateStatusById(statementId,
                        ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
                log.info("Updated statement after error: {}", statement);
            }
        } catch (EntityNotFoundException e) {
            log.error("Statement with id {} not found", statementId);
        }
    }

    private String getStatementIdFromPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

}
