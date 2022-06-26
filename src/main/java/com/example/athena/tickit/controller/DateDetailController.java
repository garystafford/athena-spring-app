package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.ecomm.DateDetail;
import com.example.athena.tickit.service.DateDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/dates")
public class DateDetailController {

    private final DateDetailService service;

    @Autowired
    public DateDetailController(DateDetailService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<DateDetail> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        return service.findAll(limit, offset);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public DateDetail findById(@PathVariable("id") int id) {

        return service.findById(id);
    }

}
