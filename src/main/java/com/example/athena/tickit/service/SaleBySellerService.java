package com.example.athena.tickit.service;

import com.example.athena.tickit.model.resultsets.SaleBySeller;

import java.util.List;

public interface SaleBySellerService {
    List<SaleBySeller> find(int id);

}