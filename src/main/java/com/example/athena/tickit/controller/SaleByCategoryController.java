package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.resultsets.SaleByCategory;
import com.example.athena.tickit.service.SaleByCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/salesbycategory")
public class SaleByCategoryController {

    private final SaleByCategoryService service;

    @Autowired
    public SaleByCategoryController(SaleByCategoryService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<SaleByCategory>> findAll(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        List<SaleByCategory> salesByCategories = service.findAll(date, limit, offset);
        if (salesByCategories.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(salesByCategories);
    }

}
