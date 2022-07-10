package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.saas.Venue;
import com.example.athena.tickit.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/venues")
public class VenueController {

    private final VenueService service;

    @Autowired
    public VenueController(VenueService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Venue>> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        List<Venue> venues = service.findAll(limit, offset);
        if (venues.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(venues);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Venue> findById(@PathVariable("id") int id) {

        Venue venue = service.findById(id);
        if (venue == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(venue);
    }

}
