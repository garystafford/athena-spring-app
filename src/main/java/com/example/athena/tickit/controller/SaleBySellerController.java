package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.resultsets.SaleBySeller;
import com.example.athena.tickit.service.SaleBySellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<SaleBySeller>> findById(@PathVariable("id") int id) {

        List<SaleBySeller> salesBySeller = service.find(id);
        if (salesBySeller.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(salesBySeller);
    }

}
