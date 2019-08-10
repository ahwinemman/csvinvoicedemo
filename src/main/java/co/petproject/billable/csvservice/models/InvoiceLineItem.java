package co.petproject.billable.csvservice.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceLineItem {
    private String employeeId;
    private BigDecimal ratePerHour;
    private int hoursWorked;
}
