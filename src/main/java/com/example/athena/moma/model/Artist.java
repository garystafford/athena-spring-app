package com.example.athena.moma.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist {

    private int id;

    private String name;

    private String gender;

    private Integer birthYear;

    private Integer deathYear;

    private String nationality;

}