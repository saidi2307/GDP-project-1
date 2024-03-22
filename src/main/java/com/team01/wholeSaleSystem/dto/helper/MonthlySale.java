package com.team01.wholeSaleSystem.dto.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySale {
    private Integer month;
    private Double amount;
    private Long numberOfOrders;
}
