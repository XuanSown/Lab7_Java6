package com.example.Lab7.controller;

import com.example.Lab7.dto.ApiResponse;
import com.example.Lab7.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Lab7.dto.ProductDTO.ProductRequest;
import com.example.Lab7.dto.ProductDTO.ProductResponse;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(
            @RequestParam(required = false) String categoryId) {

        List<ProductResponse> products;
        if (categoryId != null && !categoryId.isBlank()) {
            products = productService.getProductsByCategory(categoryId);
        } else {
            products = productService.getAllProducts();
        }
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách sản phẩm thành công", products)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Integer id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(
                        ApiResponse.success("Tìm thấy sản phẩm", product)
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy sản phẩm với mã: " + id))
                );
    }

    //Create
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        ApiResponse<ProductResponse> response = productService.createProduct(request);
        HttpStatus status = response.success() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request) {
        ApiResponse<ProductResponse> response = productService.updateProduct(id, request);
        HttpStatus status = response.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer id) {
        ApiResponse<Void> response = productService.deleteProduct(id);
        HttpStatus status = response.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
