package com.example.athena.moma.controller;

import com.example.athena.moma.model.Artwork;
import com.example.athena.moma.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/artworks")
public class ArtworkController {

    private final ArtworkService service;

    @Autowired
    public ArtworkController(ArtworkService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Artwork> findAll(
            @RequestParam(required = false) Integer artistId,
            @RequestParam(required = false) String classification,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {


        return service.findAll(artistId, classification, limit, offset);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Artwork findById(@PathVariable("id") int id) {

        return service.findById(id);
    }

}
