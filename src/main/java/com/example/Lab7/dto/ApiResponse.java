package com.example.Lab7.dto;

//DTO phản hồi chung
//success true nếu thành công, ngược lại
//data trả về null khi lỗi
public record ApiResponse<T>(boolean success, String message, T data) {
    //method phản hồi thành công với dữ liệu
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    //method phản hồi thành công khi không có liệu
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    //method pha hồi thất bại
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
