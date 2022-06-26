package com.example.athena.tickit.model.resultsets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleBySeller {

    private LocalDate calDate;

    private BigDecimal pricePaid;

    private int qtySold;

    private BigDecimal saleAmount;

    private BigDecimal commission;

    private Double commissionPrcnt;

    private String eventName;

    private String seller;

    private String buyer;

    private String catGroup;

    private String catName;
}
