package com.example.athena.tickit.service;

import com.example.athena.tickit.model.ecomm.Sale;

import java.util.List;

public interface SaleService {
    List<Sale> findAll(Integer limit, Integer offset);

    Sale findById(int id);
}