package com.dipal.NextCart.repository;


import com.dipal.NextCart.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {
}