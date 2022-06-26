package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.resultsets.BuyerLikesByCategory;
import com.example.athena.tickit.service.BuyersLikesByCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<BuyerLikesByCategory> get() {

        return service.get();
    }

}
