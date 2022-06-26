package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.ecomm.Listing;
import com.example.athena.tickit.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/listings")
public class ListingController {

    private final ListingService service;

    @Autowired
    public ListingController(ListingService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Listing> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        return service.findAll(limit, offset);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Listing findById(@PathVariable("id") int id) {

        return service.findById(id);
    }

}
