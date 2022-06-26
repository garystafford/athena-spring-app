package com.example.athena.tickit.service;

import com.example.athena.tickit.model.resultsets.SalesByCategory;

import java.util.List;

public interface SalesByCategoryService {
    List<SalesByCategory> findAll(String calendarDate, Integer limit, Integer offset);
}