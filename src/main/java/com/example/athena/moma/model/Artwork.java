package com.example.athena.moma.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artwork {

    private int id;

    private int artistId;

    private String title;

    private Integer date;

    private String medium;

    private String catalogue;

    private String department;

    private String classification;

}