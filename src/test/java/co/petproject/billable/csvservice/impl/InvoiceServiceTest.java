package co.petproject.billable.csvservice.impl;

import co.petproject.billable.csvservice.api.InvoiceRepository;
import co.petproject.billable.csvservice.api.InvoiceSourceReader;
import co.petproject.billable.csvservice.models.CompanyInvoice;
import co.petproject.billable.csvservice.models.CsvItem;
import co.petproject.billable.csvservice.models.ParseResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InvoiceServiceTest {

    InvoiceService instance;

    @Mock
    InvoiceSourceReader sourceReader;

    @Mock
    InvoiceRepository invoiceRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        instance = new InvoiceService();
        instance.sourceReader = sourceReader;
        instance.invoiceRepository = invoiceRepository;
    }

    @Test
    public void getParseResult() {
        CompanyInvoice invoice1 = CompanyInvoice.builder()
                .name("ACME1")
                .lineItems(new ArrayList<>())
                .build();

        CompanyInvoice invoice2 = CompanyInvoice.builder()
                .name("ACME2")
                .lineItems(new ArrayList<>())
                .build();

        List<CompanyInvoice> invoices = Arrays.asList(invoice1, invoice2);

        String resultId = UUID.randomUUID().toString();
        when(invoiceRepository.getInvoices(resultId)).thenReturn(invoices);
        ParseResult parseResult = instance.getParseResult(resultId);
        assertNotNull(parseResult);
        assertEquals(resultId, parseResult.getId());
        assertEquals(2, parseResult.getCompanies().size());
        assertEquals(BigDecimal.ZERO, parseResult.getCompanies().get("ACME1"));
        assertEquals(BigDecimal.ZERO, parseResult.getCompanies().get("ACME2"));
        assertNull(parseResult.getCompanies().get("ACMEX"));
    }

    @Test
    public void parseCsv() throws IOException {
        InputStream is = mock(InputStream.class);
        List<CsvItem> items = new ArrayList<>();

        items.add(getMockItem("ACME1", 1, 200.0));
        items.add(getMockItem("ACME2", 5, 100.0));
        items.add(getMockItem("ACME5", 8, 50.0));
        items.add(getMockItem("ACME2", 1, 1000.0));
        //case-sensitivity
        items.add(getMockItem("acme5", 6, 50.0));
        items.add(getMockItem("acme2", 1, 100.0));

        when(sourceReader.readRecords(is)).thenReturn(items);

        ParseResult result = instance.parseCsv(is);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(3, result.getCompanies().size());

        assertEquals(BigDecimal.valueOf(200.0), result.getCompanies().get("ACME1"));
        assertEquals(BigDecimal.valueOf(1600.0), result.getCompanies().get("ACME2"));
        assertEquals(BigDecimal.valueOf(700.0), result.getCompanies().get("ACME5"));

        
    }

    private CsvItem getMockItem(String companyName, int hours, double rate) {
        CsvItem item = new CsvItem();
        item.setHoursWorked(hours);
        item.setCompany(companyName);
        item.setEmployeeId(String.valueOf(Math.ceil(Math.random() * 10)));
        item.setDate(new Date());
        item.setRatePerHour(BigDecimal.valueOf(rate));
        return item;
    }

    @Test
    public void getInvoiceItems() {
        CompanyInvoice companyInvoice = new CompanyInvoice();

        String resultId = UUID.randomUUID().toString();
        String company = "ACME";
        Mockito.when(invoiceRepository.findInvoiceItemsByResultAndCompany(resultId, company))
                .thenReturn(companyInvoice);

        assertEquals(companyInvoice, instance.getInvoiceItems(resultId, company));
    }
}