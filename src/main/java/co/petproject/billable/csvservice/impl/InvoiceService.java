package co.petproject.billable.csvservice.impl;


import co.petproject.billable.csvservice.api.InvoiceRepository;
import co.petproject.billable.csvservice.api.InvoiceSourceReader;
import co.petproject.billable.csvservice.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceSourceReader sourceReader;

    public ParseResult getParseResult(String id) {
        List<CompanyInvoice> invoices = invoiceRepository.getInvoices(id);
        ParseResult result = ParseResult.builder()
                .id(id)
                .companies(new HashMap<>())
                .build();

        invoices.forEach((invoice) -> {
            result.getCompanies().put(invoice.getName(), invoice.getTotalAmount());
        });

        return result;
    }

    public ParseResult parseCsv(InputStream input) throws IOException {
        List<CsvItem> items = sourceReader.readRecords(input);

        //generate parse ID
        String parseId = UUID.randomUUID().toString();
        log.debug("Records read {} -> Parse ID {}", items.size(), parseId);


        //save to repository
        Map<String, CompanyInvoice> invoiceMap = new HashMap<>(items.size());

        for (CsvItem item : items) {
            String nameNormalized = item.getCompany().toLowerCase();
            CompanyInvoice companyInvoice = invoiceMap.get(nameNormalized);
            if (companyInvoice == null) {
                companyInvoice = CompanyInvoice.builder()
                        .lineItems(new ArrayList<>())
                        .name(item.getCompany())
                        .build();
                invoiceMap.put(nameNormalized, companyInvoice);
            }

            InvoiceLineItem lineItem = new InvoiceLineItem();
            BeanUtils.copyProperties(item, lineItem);
            companyInvoice.getLineItems().add(lineItem);
        }


        ParseResult parseResult = ParseResult.builder()
                .companies(new HashMap<>())
                .id(parseId)
                .build();

        invoiceMap.values().forEach((invoice) -> {
            //save
            invoiceRepository.saveResultCompanyInvoice(parseId, invoice);
            //aggregate
            parseResult.getCompanies().put(invoice.getName(), invoice.getTotalAmount());
        });
        return parseResult;
    }


    public CompanyInvoice getInvoiceItems(String parseResultId, String company) {
        return invoiceRepository.findInvoiceItemsByResultAndCompany(parseResultId, company);
    }
}
