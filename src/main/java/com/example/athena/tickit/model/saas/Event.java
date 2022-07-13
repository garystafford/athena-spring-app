package com.example.athena.tickit.model.saas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Serializable {

    private int id;

    private int venueId;

    private int catId;

    private int dateId;

    private String name;

    private LocalDateTime startTime;

}