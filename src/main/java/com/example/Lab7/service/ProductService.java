package com.example.Lab7.service;

import com.example.Lab7.dto.ApiResponse;
import com.example.Lab7.dto.ProductDTO;
import com.example.Lab7.dto.ProductDTO.ProductResponse;
import com.example.Lab7.dto.ProductDTO.ProductRequest;
import com.example.Lab7.entity.Category;
import com.example.Lab7.entity.Product;
import com.example.Lab7.repository.CategoryRepository;
import com.example.Lab7.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCreateDate(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //Lấy danh sách theo loại hàng
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String categoryId){
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //lấy theo id
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProductById(Integer id){
        return productRepository.findById(id)
                .map(this::mapToResponse);
    }

    //Create
    public ApiResponse<ProductResponse> createProduct(ProductDTO.ProductRequest request){
        if (categoryRepository.findById(request.categoryId()).isEmpty()){
            return ApiResponse.error("Mã loại hàng '" + request.categoryId() + "' không tồn tại");
        }
        if (categoryRepository.findById(request.categoryId()).isPresent() && request.price() <= 0){
            return ApiResponse.error("Giá sản phẩm phải lớn hơn 0");
        }

        Category category = categoryRepository.findById(request.categoryId()).get();
        Product newProduct = new Product(
                request.name(),
                request.price(),
                request.createDate(),
                category
        );
        Product savedProduct = productRepository.save(newProduct);
        return ApiResponse.success("Tạo sản phẩm thành công",
                mapToResponse(savedProduct));
    }

    //Update
    public ApiResponse<ProductResponse> updateProduct(Integer id,ProductRequest request){
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            return ApiResponse.error("Không tìm thấy sản phẩm với mã: " + id);
        }

        //ktr loại hàng mới có tồn tại không
        if (categoryRepository.findById(request.categoryId()).isEmpty()){
            return ApiResponse.error("Mã loại hàng '" + request.categoryId() + "' không tồn tại");
        }
        Product product = existingProduct.get();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setCreateDate(request.createDate());
        product.setCategory(categoryRepository.findById(request.categoryId()).get());

        Product updatedProduct = productRepository.save(product);

        return ApiResponse.success(
                "Cập nhật sản phẩm thành công",
                mapToResponse(updatedProduct)
        );
    }

    //Delete
    public ApiResponse<Void> deleteProduct(Integer id) {

        if (!productRepository.existsById(id)) {
            return ApiResponse.error("Không tìm thấy sản phẩm với mã: " + id);
        }

        productRepository.deleteById(id);
        return ApiResponse.success("Xóa sản phẩm thành công");
    }
}
