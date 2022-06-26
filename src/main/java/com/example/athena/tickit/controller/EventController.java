package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.saas.Event;
import com.example.athena.tickit.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/events")
public class EventController {

    private final EventService service;

    @Autowired
    public EventController(EventService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Event> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        return service.findAll(limit, offset);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Event findById(@PathVariable("id") int id) {

        return service.findById(id);
    }

}
