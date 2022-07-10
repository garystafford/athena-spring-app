package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.ecomm.Sale;
import com.example.athena.tickit.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sales")
public class SaleController {

    private final SaleService service;

    @Autowired
    public SaleController(SaleService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Sale>> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        List<Sale> sales = service.findAll(limit, offset);
        if (sales.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(sales);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Sale> findById(@PathVariable("id") int id) {

        Sale sale = service.findById(id);
        if (sale == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(sale);
    }

}
