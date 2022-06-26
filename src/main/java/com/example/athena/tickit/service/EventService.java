package com.example.athena.tickit.service;

import com.example.athena.tickit.model.saas.Event;

import java.util.List;

public interface EventService {
    List<Event> findAll(Integer limit, Integer offset);

    Event findById(int id);
}