package co.petproject.billable.csvservice.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class CsvItem extends InvoiceLineItem {
    private String company;
    private Date date;
}
