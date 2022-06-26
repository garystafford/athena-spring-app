package com.example.athena.tickit.model.ecomm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    private int id;

    private int listId;

    private int sellerId;

    private int buyerId;

    private int eventId;

    private int dateId;

    private int qtySold;

    private BigDecimal pricePaid;

    private BigDecimal commission;

    private LocalDateTime saleTime;

}