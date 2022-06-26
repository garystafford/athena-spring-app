package com.example.athena.tickit.service;

import com.example.athena.tickit.model.ecomm.DateDetail;

import java.util.List;

public interface DateDetailService {
    List<DateDetail> findAll(Integer limit, Integer offset);

    DateDetail findById(int id);
}