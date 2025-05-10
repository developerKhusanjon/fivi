package me.fivi.dto

import groovy.transform.CompileStatic
import io.micronaut.core.annotation.Introspected

@CompileStatic
@Introspected
class ApiResponse<T> {
    boolean success
    String message
    T data

    static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(success: true, message: "Success", data: data)
    }

    static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>(success: true, message: message, data: data)
    }

    static ApiResponse<Void> success(String message) {
        return new ApiResponse<Void>(success: true, message: message, data: null)
    }

    static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<T>(success: false, message: message, data: null)
    }
}