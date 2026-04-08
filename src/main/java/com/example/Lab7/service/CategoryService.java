package com.example.Lab7.service;

import com.example.Lab7.dto.ApiResponse;
import com.example.Lab7.dto.CategoryDTO.CategoryRequest;
import com.example.Lab7.dto.CategoryDTO.CategoryResponse;
import com.example.Lab7.entity.Category;
import com.example.Lab7.repository.CategoryRepository;
import com.example.Lab7.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }

    //l?y toàn b? danh sách lo?i hàng
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //tìm theo mã lo?i hàng
    @Transactional(readOnly = true)
    public Optional<CategoryResponse> getCategoryById(String id) {
        return categoryRepository.findById(id)
                .map(this::mapToResponse);
    }

    //Create
    //request dl lo?i hàng t? frontend
    //ApiResponse chứa kết quả
    public ApiResponse<CategoryResponse> createCategory(CategoryRequest request) {
        if (categoryRepository.existsById(request.id())) {
            return ApiResponse.error("Mã loại hàng" + request.id() + "đã tồn tại");
        }
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            return ApiResponse.error("Tên loại hàng" + request.name() + "đã tồn tại");
        }

        Category newCategory = new Category(request.id(), request.name());
        Category savedCategory = categoryRepository.save(newCategory);

        return ApiResponse.success(
                "Tạo loại hàng thành công",
                mapToResponse(savedCategory)
        );
    }

    //Update
    //ApiResponse chứa kết quả
    public ApiResponse<CategoryResponse> updateCategory(String id, CategoryRequest request) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            return ApiResponse.error("Khoong tìm thấy loại hàng với mã" + id);
        }

        Category category = existingCategory.get();
        boolean nameConflict = categoryRepository.existsByNameIgnoreCase(request.name())
                && !category.getName().equalsIgnoreCase(request.name());
        if (nameConflict) {
            return ApiResponse.error("Tên loại hàng '" + request.name() + "' đã được sử dụng");
        }

        category.setName(request.name());
        Category updatedCategory = categoryRepository.save(category);

        return ApiResponse.success(
                "Cập nhật loại hàng thành công",
                mapToResponse(updatedCategory)
        );
    }

    //Delete
    //nếu còn sản phẩm thuộc loại này thì từ chối xóa
    //ApiResponse chứa kết quả
    public ApiResponse<Void> deleteCategory(String id){
        //ktr loại hàng còn tồn tại hay không
        if (!categoryRepository.existsById(id)){
            return ApiResponse.error("Không tìm thây loại hàng" + id);
        }
        //ktr sản phẩm có đang còn loại hàng này không
        if (productRepository.existsByCategoryId(id)){
            return ApiResponse.error("Không thể xóa loại hàng này vì đang có sản phẩm thuộc loại đó");
        }
        categoryRepository.deleteById(id);
        return ApiResponse.success("Xóa thành công");
    }
}
