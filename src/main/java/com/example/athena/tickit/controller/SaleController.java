package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.ecomm.Sale;
import com.example.athena.tickit.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Sale> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        return service.findAll(limit, offset);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Sale findById(@PathVariable("id") int id) {

        return service.findById(id);
    }

}
