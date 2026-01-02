package com.example.ticket.global.exception;

import com.example.ticket.global.dto.ApiResponse;
import com.example.ticket.global.dto.ErrorResponse;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleApi(ApiException e){
        ErrorCode code=e.getErrorCode();
        ErrorResponse error=new ErrorResponse(code.name(),code.getMessage());
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.fail(error));

    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity< ApiResponse<ErrorResponse>> handleValid(MethodArgumentNotValidException e){
        String message=  e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                :  ErrorCode.INVALID_REQUEST.getMessage();
        ;
        ErrorResponse error = new ErrorResponse(ErrorCode.INVALID_REQUEST.name(), message);

        return ResponseEntity.badRequest().body(ApiResponse.fail(error));}
    @ExceptionHandler(IllegalArgumentException.class)
    public  ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException e){
        ErrorResponse error = new ErrorResponse(
                ErrorCode.INVALID_REQUEST.name(),
                e.getMessage()
        );
       return ResponseEntity.badRequest()
                .body(ApiResponse.fail(error));
        }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleEtc(Exception e) {
        // 운영에서는 로깅 추가 추천
        ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.");
        return ResponseEntity.status(500).body(ApiResponse.fail(error));
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleState(IllegalStateException e) {
        ErrorResponse error = new ErrorResponse(ErrorCode.INVALID_REQUEST.name(), e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.fail(error));
    }

    // ✅ Day5 추가: 동시성 충돌(Optimistic Lock)
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleOptimistic(OptimisticLockingFailureException e) {
        ErrorResponse error = new ErrorResponse(ErrorCode.CONCURRENT_REQUEST.name(),
                ErrorCode.CONCURRENT_REQUEST.getMessage());
        return ResponseEntity.status(ErrorCode.CONCURRENT_REQUEST.getStatus()).body(ApiResponse.fail(error));
    }



}
