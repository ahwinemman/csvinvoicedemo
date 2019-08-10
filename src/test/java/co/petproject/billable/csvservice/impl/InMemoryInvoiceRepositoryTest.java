package co.petproject.billable.csvservice.impl;

import co.petproject.billable.csvservice.models.CompanyInvoice;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class InMemoryInvoiceRepositoryTest {


    InMemoryInvoiceRepository instance;

    @Before
    public void setUp() throws Exception {
        instance = new InMemoryInvoiceRepository();
        
    }

    @Test
    public void saveFindAndGetResult() {
        String result1 = UUID.randomUUID().toString();
        //verify empty
        assertTrue(instance.getInvoices(result1).isEmpty());
        instance.saveResultCompanyInvoice(result1, CompanyInvoice.builder().name("ACME1").build());
        instance.saveResultCompanyInvoice(result1, CompanyInvoice.builder().name("AIM1").build());
        instance.saveResultCompanyInvoice(result1, CompanyInvoice.builder().name("F-Society").build());

        String result2 = UUID.randomUUID().toString();
        assertTrue(instance.getInvoices(result2).isEmpty());
        instance.saveResultCompanyInvoice(result2, CompanyInvoice.builder().name("ACME2").build());
        instance.saveResultCompanyInvoice(result2, CompanyInvoice.builder().name("AIM2").build());

        assertNotNull(instance.findInvoiceItemsByResultAndCompany(result1, "acme1"));
        assertNotNull(instance.findInvoiceItemsByResultAndCompany(result1, "ACME1"));
        assertNull(instance.findInvoiceItemsByResultAndCompany(result2, "ACME1"));
        assertEquals(3, instance.getInvoices(result1).size());


        assertNotNull(instance.findInvoiceItemsByResultAndCompany(result2, "acme2"));
        assertNotNull(instance.findInvoiceItemsByResultAndCompany(result2, "ACME2"));
        assertNull(instance.findInvoiceItemsByResultAndCompany(result1, "ACME2"));
        assertEquals(2, instance.getInvoices(result2).size());


    }
}