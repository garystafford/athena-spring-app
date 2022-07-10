package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.ecomm.Listing;
import com.example.athena.tickit.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Listing>> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        List<Listing> listings = service.findAll(limit, offset);
        if (listings.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(listings);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Listing> findById(@PathVariable("id") int id) {

        Listing listing = service.findById(id);
        if (listing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(listing);
    }

}
