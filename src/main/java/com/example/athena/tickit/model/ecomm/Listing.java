package com.example.athena.tickit.model.ecomm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Listing implements Serializable {

    private int id;

    private int sellerId;

    private int eventId;

    private int dateId;

    private int numTickets;

    private BigDecimal pricePerTicket;

    private LocalDateTime listTime;

}