package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.resultsets.BuyerLikesByCategory;
import com.example.athena.tickit.service.BuyersLikesByCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/buyerlikes")
public class BuyerLikesByCategoryController {

    private final BuyersLikesByCategoryService service;

    @Autowired
    public BuyerLikesByCategoryController(BuyersLikesByCategoryService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BuyerLikesByCategory>> get() {

        List<BuyerLikesByCategory> buyerLikesByCategories = service.get();
        if (buyerLikesByCategories.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(buyerLikesByCategories);
    }

}
