package com.example.athena.tickit.model.ecomm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateDetail implements Serializable {

    private int id;

    private LocalDate calendarDate;

    private String day;

    private String week;

    private String month;

    private String quarter;

    private int year;

    private boolean holiday;

}