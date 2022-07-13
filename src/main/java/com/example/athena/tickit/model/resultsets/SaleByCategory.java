package com.example.athena.tickit.model.resultsets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleByCategory implements Serializable {

    private LocalDate calendarDate;

    private String categoryGroup;

    private String categoryName;

    private BigDecimal saleAmount;

    private BigDecimal commission;

}
