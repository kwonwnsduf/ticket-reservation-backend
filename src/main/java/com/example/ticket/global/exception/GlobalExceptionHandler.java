package com.example.ticket.global.exception;

import com.example.ticket.global.dto.ApiResponse;
import com.example.ticket.global.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleApi(ApiException e){
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.fail( new ErrorResponse(e.getMessage())));

    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity< ApiResponse<ErrorResponse>> handleValid(MethodArgumentNotValidException e){
        String message=  e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "요청 값이 올바르지 않습니다.";

        return ResponseEntity.badRequest().body(ApiResponse.fail(new ErrorResponse(message)));}
    @ExceptionHandler(IllegalArgumentException.class)
    public  ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException e){
       return ResponseEntity.badRequest()
                .body(ApiResponse.fail(new ErrorResponse(e.getMessage())));
        }
}
