package zerobase.dividends.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstractException e) {
        log.error("{} is occurred. code & message -> {}, {}",
                e.getClass().getSimpleName(), e.getStatusCode(),
                e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getStatusCode())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse,
                HttpStatus.resolve(e.getStatusCode()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access denied exception is occurred.", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse,
                HttpStatus.resolve(HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception is occurred.", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse,
                HttpStatus.resolve(HttpStatus.BAD_REQUEST.value()));
    }
}
