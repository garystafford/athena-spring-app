package com.example.athena.tickit.model.saas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue implements Serializable {

    private int id;

    private String name;

    private String city;

    private String state;

    private Integer seats;

}