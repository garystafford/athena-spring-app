package com.example.athena.moma.controller;

import com.example.athena.moma.model.Artist;
import com.example.athena.moma.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/artists")
public class ArtistController {

    private final ArtistService service;

    @Autowired
    public ArtistController(ArtistService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Artist> findAll(
            @RequestParam(required = false) String nationality,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        return service.findAll(nationality, limit, offset);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Artist findById(@PathVariable("id") int id) {

        return service.findById(id);
    }

}
