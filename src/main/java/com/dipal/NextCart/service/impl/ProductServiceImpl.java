package com.dipal.NextCart.service.impl;



import com.dipal.NextCart.dto.ProductDTO;
import com.dipal.NextCart.dto.Response;
import com.dipal.NextCart.entity.Category;
import com.dipal.NextCart.entity.Product;
import com.dipal.NextCart.exception.FileStorageException;
import com.dipal.NextCart.exception.NotFoundException;
import com.dipal.NextCart.mapper.EntityDtoMapper;
import com.dipal.NextCart.repository.CategoryRepo;
import com.dipal.NextCart.repository.OrderItemRepo;
import com.dipal.NextCart.repository.ProductRepo;
import com.dipal.NextCart.service.FileStorageService;
import com.dipal.NextCart.service.interfce.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final FileStorageService fileStorageService;
    private final OrderItemRepo orderItemRepo;

    @Override
    public Response createProduct(Long categoryId, MultipartFile image, String name,
                                  String description, BigDecimal price) {
        try {
            validateProductInputs(categoryId, image, name, description, price);

            // Upload image to Cloudinary and get URL
            String imageUrl = fileStorageService.saveFile(image);

            Category category = categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category not found with ID: " + categoryId));

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setImageUrl(imageUrl);  // Save Cloudinary URL here
            product.setCategory(category);

            Product savedProduct = productRepo.save(product);
            ProductDTO productDTO = entityDtoMapper.mapProductToDtoBasic(savedProduct);

            return Response.builder()
                    .status(200)
                    .message("Product created successfully")
                    .product(productDTO)
                    .build();

        } catch (NotFoundException e) {
            log.error("Category not found error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating product: ", e);
            throw new RuntimeException("Error creating product: " + e.getMessage(), e);
        }
    }


    @Override
    public Response updateProduct(Long productId, Long categoryId, MultipartFile image,
                                  String name, String description, BigDecimal price) {
        try {
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));

            if (categoryId != null) {
                Category category = categoryRepo.findById(categoryId)
                        .orElseThrow(() -> new NotFoundException("Category not found with ID: " + categoryId));
                product.setCategory(category);
            }

            if (image != null && !image.isEmpty()) {
                String imageUrl = fileStorageService.saveFile(image);
                product.setImageUrl(imageUrl);
            }


            if (name != null && !name.trim().isEmpty()) {
                product.setName(name);
            }
            if (description != null && !description.trim().isEmpty()) {
                product.setDescription(description);
            }
            if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                product.setPrice(price);
            }

            Product updatedProduct = productRepo.save(product);
            ProductDTO productDTO = entityDtoMapper.mapProductToDtoBasic(updatedProduct);

            return Response.builder()
                    .status(200)
                    .message("Product updated successfully")
                    .product(productDTO)
                    .build();

        } catch (NotFoundException e) {
            log.error("Resource not found error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating product: ", e);
            throw new FileStorageException("Error updating product: " + e.getMessage(), e);
        }
    }


    @Override
    public Response deleteProduct(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));

        product.setDeleted(true);
        productRepo.save(product);

        return Response.builder()
                .status(200)
                .message("Product soft deleted successfully")
                .build();
    }


    @Override
    public Response getProductById(Long productId) {
        try {
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));

            ProductDTO productDto = entityDtoMapper.mapProductToDtoBasic(product);

            return Response.builder()
                    .status(200)
                    .product(productDto)
                    .build();
        } catch (NotFoundException e) {
            log.error("Product not found error: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    public Response getAllProducts() {
        List<ProductDTO> productList = productRepo.findByDeletedFalse()
                .stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .productList(productList)
                .build();
    }


    @Override
    public Response getProductsByCategory(Long categoryId) {
        try {
            List<Product> products = productRepo.findByCategoryId(categoryId);
            if (products.isEmpty()) {
                throw new NotFoundException("No products found for category ID: " + categoryId);
            }

            List<ProductDTO> productDtoList = products.stream()
                    .map(entityDtoMapper::mapProductToDtoBasic)
                    .collect(Collectors.toList());

            return Response.builder()
                    .status(200)
                    .productList(productDtoList)
                    .build();
        } catch (NotFoundException e) {
            log.error("No products found error: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Response searchProduct(String searchValue) {
        try {
            if (searchValue == null || searchValue.trim().isEmpty()) {
                throw new IllegalArgumentException("Search value cannot be empty");
            }

            List<Product> products = productRepo.findByNameContainingOrDescriptionContaining(
                    searchValue.trim(), searchValue.trim());

            if (products.isEmpty()) {
                throw new NotFoundException("No products found matching: " + searchValue);
            }

            List<ProductDTO> productDtoList = products.stream()
                    .map(entityDtoMapper::mapProductToDtoBasic)
                    .collect(Collectors.toList());

            return Response.builder()
                    .status(200)
                    .productList(productDtoList)
                    .build();
        } catch (Exception e) {
            log.error("Error searching products: ", e);
            throw e;
        }
    }

    // Helper method to validate product inputs
    private void validateProductInputs(Long categoryId, MultipartFile image,
                                       String name, String description, BigDecimal price) {
        List<String> errors = new ArrayList<>();

        if (categoryId == null) {
            errors.add("Category ID is required");
        }
        if (image == null || image.isEmpty()) {
            errors.add("Product image is required");
        }
        if (name == null || name.trim().isEmpty()) {
            errors.add("Product name is required");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.add("Product description is required");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Valid product price is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
        }
    }
}
