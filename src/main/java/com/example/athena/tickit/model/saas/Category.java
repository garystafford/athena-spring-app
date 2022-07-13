package com.example.athena.tickit.model.saas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {

    private int id;

    private String group;

    private String name;

    private String description;

}