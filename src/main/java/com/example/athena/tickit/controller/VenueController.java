package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.saas.Venue;
import com.example.athena.tickit.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Venue> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        return service.findAll(limit, offset);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Venue findById(@PathVariable("id") int id) {

        return service.findById(id);
    }

}
