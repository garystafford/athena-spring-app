package com.example.athena.moma.service;

import com.example.athena.moma.model.Artist;

import java.util.List;

public interface ArtistService {
    List<Artist> findAll(String nationality, Integer limit, Integer offset);

    Artist findById(int id);
}