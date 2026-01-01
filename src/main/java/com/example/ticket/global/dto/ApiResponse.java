package com.example.ticket.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private ErrorResponse error;
    public static <T> ApiResponse<T> ok(T data){
        return new ApiResponse<>(true,data,null);
    }
    public static <T> ApiResponse<T> fail(ErrorResponse error){
        return new ApiResponse<>(false,null,error);

    }

}
