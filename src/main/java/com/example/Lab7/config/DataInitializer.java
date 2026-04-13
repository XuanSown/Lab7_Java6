package com.example.Lab7.config;

import com.example.Lab7.entity.Category;
import com.example.Lab7.entity.Product;
import com.example.Lab7.repository.CategoryRepository;
import com.example.Lab7.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {
    public CommandLineRunner seedSampleData(
            CategoryRepository categoryRepository,
            ProductRepository productRepository) {

        return args -> {
            System.out.println(">>> Bắt đầu khởi tạo dữ liệu mẫu...");

            // ==========================================
            // Tạo dữ liệu mẫu Loại hàng (Category)
            // ==========================================
            Category electronics = new Category("DIEN-TU", "Điện tử");
            Category clothing    = new Category("THOI-TRANG", "Thời trang");
            Category food        = new Category("THUC-PHAM", "Thực phẩm");
            Category books       = new Category("SACH", "Sách & Văn phòng phẩm");

            categoryRepository.save(electronics);
            categoryRepository.save(clothing);
            categoryRepository.save(food);
            categoryRepository.save(books);

            System.out.println(">>> Đã tạo " + categoryRepository.count() + " loại hàng");

            // ==========================================
            // Tạo dữ liệu mẫu Sản phẩm (Product)
            // ==========================================
            productRepository.save(new Product("iPhone 15 Pro Max 256GB", 34990000.0,
                    LocalDate.of(2024, 1, 10), electronics));

            productRepository.save(new Product("Samsung Galaxy S24 Ultra", 29990000.0,
                    LocalDate.of(2024, 2, 1), electronics));

            productRepository.save(new Product("Laptop ASUS VivoBook 15", 15990000.0,
                    LocalDate.of(2024, 1, 20), electronics));

            productRepository.save(new Product("Tai nghe Sony WH-1000XM5", 7490000.0,
                    LocalDate.of(2024, 3, 5), electronics));

            productRepository.save(new Product("Áo Polo Nam Premium Cotton", 450000.0,
                    LocalDate.of(2024, 2, 14), clothing));

            productRepository.save(new Product("Quần Jeans Nữ Slim Fit", 680000.0,
                    LocalDate.of(2024, 2, 20), clothing));

            productRepository.save(new Product("Gạo ST25 Túi 5kg", 85000.0,
                    LocalDate.of(2024, 3, 1), food));

            productRepository.save(new Product("Cà phê Highlands Ground 500g", 125000.0,
                    LocalDate.of(2024, 3, 10), food));

            productRepository.save(new Product("Clean Code - Robert C. Martin", 320000.0,
                    LocalDate.of(2024, 1, 5), books));

            productRepository.save(new Product("Design Patterns - GoF", 280000.0,
                    LocalDate.of(2024, 1, 15), books));

            System.out.println(">>> Đã tạo " + productRepository.count() + " sản phẩm");
            System.out.println(">>> ✅ Khởi tạo dữ liệu mẫu hoàn tất!");
            System.out.println(">>> 📌 API Docs:");
            System.out.println("    GET  http://localhost:8080/api/categories");
            System.out.println("    GET  http://localhost:8080/api/products");
            System.out.println("    H2 Console: http://localhost:8080/h2-console");
        };
    }
}
