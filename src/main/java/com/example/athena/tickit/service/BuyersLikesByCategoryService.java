package com.example.athena.tickit.service;

import com.example.athena.tickit.model.resultsets.BuyerLikesByCategory;

import java.util.List;

public interface BuyersLikesByCategoryService {
    List<BuyerLikesByCategory> get();
}