package co.petproject.billable.csvservice.impl;

import co.petproject.billable.csvservice.api.InvoiceRepository;
import co.petproject.billable.csvservice.api.ResultSaveException;
import co.petproject.billable.csvservice.models.CompanyInvoice;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryInvoiceRepository implements InvoiceRepository {

    private final Map<String, CompanyInvoice> STORE = new ConcurrentHashMap<>();


    private String getKey(String resultId, String name) {
        return String.format("%s|%s", resultId, name.toLowerCase());
    }

    @Override
    public CompanyInvoice findInvoiceItemsByResultAndCompany(String resultId, String company) {
        String key = getKey(resultId, company);
        return STORE.get(key);
    }

    @Override
    public void saveResultCompanyInvoice(String resultId, CompanyInvoice companyInvoice) throws ResultSaveException {
        String key = getKey(resultId, companyInvoice.getName());
        STORE.put(key, companyInvoice);
    }

    @Override
    public List<CompanyInvoice> getInvoices(String resultId) {
        List<CompanyInvoice> companyInvoices = new ArrayList<>();
        STORE.forEach((key, invoice) -> {
            if (key.startsWith(resultId)) {
                companyInvoices.add(invoice);
            }
        });
        
        return companyInvoices;
    }
}
