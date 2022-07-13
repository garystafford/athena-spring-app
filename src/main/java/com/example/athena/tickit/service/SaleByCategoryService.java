package com.example.athena.tickit.service;

import com.example.athena.tickit.model.resultsets.SaleByCategory;

import java.util.List;

public interface SaleByCategoryService {
    List<SaleByCategory> findAll(String calendarDate, Integer limit, Integer offset);
}