package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.resultsets.SaleBySeller;
import com.example.athena.tickit.service.SaleBySellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/salesbyseller")
public class SaleBySellerController {


    private final SaleBySellerService service;

    @Autowired
    public SaleBySellerController(SaleBySellerService service) {
        this.service = service;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public List<SaleBySeller> findById(@PathVariable("id") int id) {

        return service.find(id);
    }

}
