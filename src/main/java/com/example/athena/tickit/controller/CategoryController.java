package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.saas.Category;
import com.example.athena.tickit.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    private final CategoryService service;

    @Autowired
    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Category> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        return service.findAll(limit, offset);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Category findById(@PathVariable("id") int id) {

        return service.findById(id);
    }

}
