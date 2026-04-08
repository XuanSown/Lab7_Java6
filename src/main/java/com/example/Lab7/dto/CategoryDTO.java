package com.example.Lab7.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDTO {
    public record CategoryRequest(
            @NotBlank(message = "Mã loại hàng không được để trống")
            @Size(max = 100, message = "Mã loại hàng không được vượt quá 50 ký tự")
            String id,

            @NotBlank(message = "Tên loại hàng không được để trống")
            @Size(max = 100, message = "Tên loại hàng tối đa 100 ký tự")
            String name
    ) {
    }

    public record CategoryResponse(
            String id,
            String name
    ) {
    }
}
