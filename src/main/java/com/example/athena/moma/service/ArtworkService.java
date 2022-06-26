package com.example.athena.moma.service;

import com.example.athena.moma.model.Artwork;

import java.util.List;

public interface ArtworkService {
    List<Artwork> findAll(Integer artistId, String classification, Integer limit, Integer offset);

    Artwork findById(int id);
}