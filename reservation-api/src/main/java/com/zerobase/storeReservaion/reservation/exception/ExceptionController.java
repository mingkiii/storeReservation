package com.zerobase.storeReservaion.reservation.exception;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.VALIDATION_ERROR;

import com.fasterxml.jackson.core.JsonParseException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ExceptionResponse> customRequestException(final CustomException c) {
        log.warn("api Exception : {}", c.getErrorCode());
        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(c.getMessage(), c.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append(fieldError.getDefaultMessage()).append("; ");
        }

        log.warn("Validation Exception: {}", errorMessage);

        return ResponseEntity.badRequest().body(new ExceptionResponse(errorMessage.toString(), VALIDATION_ERROR));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtExceptions() {
        ErrorCode errorCode = ErrorCode.TOKEN_EXPIRED;
        String errorMessage = errorCode.getMessage();

        ExceptionResponse response = new ExceptionResponse(errorMessage, errorCode);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ExceptionResponse> handleJsonParseException() {
        ErrorCode errorCode = ErrorCode.INVALID_TOKEN;
        String errorMessage = errorCode.getMessage();

        ExceptionResponse response = new ExceptionResponse(errorMessage, errorCode);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class ExceptionResponse {
        private String message;
        private ErrorCode errorCode;
    }
}
