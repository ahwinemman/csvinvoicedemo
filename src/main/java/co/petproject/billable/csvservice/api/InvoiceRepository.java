package co.petproject.billable.csvservice.api;

import co.petproject.billable.csvservice.models.CompanyInvoice;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface InvoiceRepository {

    /**
     * Returns company invoice matched by result ID and company name.
     * Company name must be case-insensitive.
     *
     * @param resultId ID of parse result
     * @param company  name of the company
     * @return the company invoice or null if not found
     */
    CompanyInvoice findInvoiceItemsByResultAndCompany(String resultId, String company);

    /**
     * Persists the result ID  for a particular CSV file.
     *
     * @param resultId ID generated for the parse.
     * @param companyInvoice  Invoice  of the company with items
     * @throws ResultSaveException when persistence fails.
     */
    void saveResultCompanyInvoice(String resultId, CompanyInvoice companyInvoice) throws ResultSaveException;

    /**
     * Gets a list of all invoices generated for a single upload
     * @param resultId parse result
     * @return list of company invoices, empty list if none.
     */
    @NotNull
    List<CompanyInvoice> getInvoices(String resultId);
}
