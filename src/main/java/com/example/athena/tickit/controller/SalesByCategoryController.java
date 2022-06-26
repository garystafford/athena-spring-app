package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.resultsets.SalesByCategory;
import com.example.athena.tickit.service.SalesByCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/salesbycategory")
public class SalesByCategoryController {

    private final SalesByCategoryService service;

    @Autowired
    public SalesByCategoryController(SalesByCategoryService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<SalesByCategory> findAll(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        return service.findAll(date, limit, offset);
    }

}
