package com.example.athena.tickit.service;

import com.example.athena.tickit.model.saas.Venue;

import java.util.List;

public interface VenueService {
    List<Venue> findAll(Integer limit, Integer offset);

    Venue findById(int id);
}