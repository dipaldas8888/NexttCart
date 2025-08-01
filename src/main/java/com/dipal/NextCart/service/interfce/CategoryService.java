package com.dipal.NextCart.service.interfce;


import com.dipal.NextCart.dto.CategoryDTO;
import com.dipal.NextCart.dto.Response;

public interface CategoryService {

    Response createCategory(CategoryDTO categoryRequest);
    Response updateCategory(Long categoryId, CategoryDTO categoryRequest);
    Response getAllCategories();
    Response getCategoryById(Long categoryId);
    Response deleteCategory(Long categoryId);
}
