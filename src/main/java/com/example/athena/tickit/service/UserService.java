package com.example.athena.tickit.service;

import com.example.athena.tickit.model.crm.User;

import java.util.List;

public interface UserService {
    List<User> findAll(Integer limit, Integer offset);

    User findById(int id);
}