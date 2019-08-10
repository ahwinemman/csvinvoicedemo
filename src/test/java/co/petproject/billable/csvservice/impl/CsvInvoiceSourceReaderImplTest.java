package co.petproject.billable.csvservice.impl;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.*;

public class CsvInvoiceSourceReaderImplTest {

    CsvInvoiceSourceReaderImpl instance;

    @Before
    public void setUp() {
        instance = new CsvInvoiceSourceReaderImpl();
    }

    @Test
    public void readRecords() {
    }

    @Test
    public void getItemFromLine() {
    }

    @Test
    public void getHoursPassed() throws ParseException {
        assertEquals(2, instance.getHoursPassed("03:00", "05:00"));
        assertEquals(6, instance.getHoursPassed("11:00", "17:00"));
        assertEquals(5, instance.getHoursPassed("11:30", "17:00"));
    }

    @Test(expected = ParseException.class)
    public void getHoursPassed_InvalidTime() throws ParseException {
        assertEquals(2, instance.getHoursPassed("53:00", "05:00"));
    }
}