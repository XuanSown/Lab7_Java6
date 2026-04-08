package com.example.Lab7.controller;

import com.example.Lab7.dto.ApiResponse;
import com.example.Lab7.dto.CategoryDTO.CategoryRequest;
import com.example.Lab7.dto.CategoryDTO.CategoryResponse;
import com.example.Lab7.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách loại hàng thành công", categories)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id)
                .map(category -> ResponseEntity.ok(
                        ApiResponse.success("Tìm thấy loại hàng", category)
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy loại hàng với mã: " + id))
                );
    }

    //Create
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        ApiResponse<CategoryResponse> response = categoryService.createCategory(request);
        HttpStatus status = response.success() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest request) {

        ApiResponse<CategoryResponse> response = categoryService.updateCategory(id, request);

        HttpStatus status = response.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        ApiResponse<Void> response = categoryService.deleteCategory(id);

        HttpStatus status = response.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
