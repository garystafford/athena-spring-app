package com.example.athena.tickit.service;

import com.example.athena.tickit.model.ecomm.Listing;

import java.util.List;

public interface ListingService {
    List<Listing> findAll(Integer limit, Integer offset);

    Listing findById(int id);
}