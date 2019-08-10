package co.petproject.billable.csvservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyInvoice {

    private String name;
    private List<InvoiceLineItem> lineItems;

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        if (lineItems != null && !lineItems.isEmpty()) {
            for (InvoiceLineItem lineItem : lineItems) {
                total = total.add(lineItem.getRatePerHour().multiply(BigDecimal.valueOf(lineItem.getHoursWorked())));
            }
        }

        return total;
    }
}
