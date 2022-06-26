package com.example.athena.tickit.service;

import com.example.athena.tickit.model.saas.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll(Integer limit, Integer offset);

    Category findById(int id);
}