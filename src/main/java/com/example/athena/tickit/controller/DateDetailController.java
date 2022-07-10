package com.example.athena.tickit.controller;

import com.example.athena.tickit.model.ecomm.DateDetail;
import com.example.athena.tickit.service.DateDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<DateDetail>> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {

        List<DateDetail> dateDetails = service.findAll(limit, offset);
        if (dateDetails.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(dateDetails);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<DateDetail> findById(@PathVariable("id") int id) {

        DateDetail dateDetail = service.findById(id);
        if (dateDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(dateDetail);
    }

}
