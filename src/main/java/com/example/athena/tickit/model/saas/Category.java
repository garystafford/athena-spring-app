package com.example.athena.tickit.model.saas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private int id;

    private String group;

    private String name;

    private String description;

}