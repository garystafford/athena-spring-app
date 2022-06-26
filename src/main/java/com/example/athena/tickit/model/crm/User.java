package com.example.athena.tickit.model.crm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;

    private String username;

    private String firstName;

    private String lastName;

    private String city;

    private String state;

    private String email;

    private String phone;

    private Boolean likeSports;

    private Boolean likeTheatre;

    private Boolean likeConcerts;

    private Boolean likeJazz;

    private Boolean likeClassical;

    private Boolean likeOpera;

    private Boolean likeRock;

    private Boolean likeVegas;

    private Boolean likeBroadway;

    private Boolean likeMusicals;

}