package com.example.athena.tickit.model.resultsets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyerLikesByCategory implements Serializable {

    private int sports;

    private int theatre;

    private int concerts;

    private int jazz;

    private int classical;

    private int opera;

    private int rock;

    private int vegas;

    private int broadway;

    private int musicals;
}
