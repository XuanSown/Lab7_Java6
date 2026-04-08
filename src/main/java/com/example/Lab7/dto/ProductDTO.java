package com.example.Lab7.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class ProductDTO {
    public record ProductRequest(
            @NotBlank(message = "Tên sản phẩm không được để trống")
            @Size(max = 150, message = "Tên sản phẩm tối đa 150 ký tự")
            String name,

            @NotNull(message = "Giá sản phẩm không được để trống")
            @Positive(message = "Giá sản phẩm phải lớn hơn 0")
            Double price,

            LocalDate createDate,

            @NotBlank(message = "Mã loại hàng không được để trống")
            String categoryId
    ) {
    }

    public record ProductResponse(
            Integer id,
            String name,
            Double price,
            LocalDate createDate,
            String categoryId,
            String categoryName
    ) {
    }

}
