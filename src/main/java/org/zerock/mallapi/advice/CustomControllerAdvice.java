package org.zerock.mallapi.advice;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zerock.mallapi.util.CustomJWTException;

import java.util.Map;
import java.util.NoSuchElementException;

// 공통이기때문에 TodoControllerAdvice가 아닌 Custom으로 이름을 생성
@RestControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<?> notExists(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> notExists(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(CustomJWTException.class)
    protected ResponseEntity<?> handleJWTException(CustomJWTException e) {
        return ResponseEntity.ok()
                .body(Map.of("error", e.getMessage()));
    }
}
