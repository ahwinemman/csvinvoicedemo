package co.petproject.billable.csvservice.impl;

import co.petproject.billable.csvservice.models.CsvItem;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;

public class CsvInvoiceSourceReaderImplTest {

    CsvInvoiceSourceReaderImpl instance;

    @Before
    public void setUp() {
        instance = new CsvInvoiceSourceReaderImpl();
    }

    @Test
    public void readRecords() throws IOException {
        InputStream csvInput = getClass().getClassLoader().getResourceAsStream("static/test.csv");
        assertNotNull(csvInput);
        List<CsvItem> items = instance.readRecords(csvInput);
        assertEquals(2, items.size());
        assertEquals("Google", items.get(0).getCompany());
        assertEquals("Facebook", items.get(1).getCompany());
    }

    @Test
    public void getItemFromLine() throws IOException {
        CSVRecord record = getRecord("1,300,Google,2019-07-01,09:00,17:00");
        CsvItem item = instance.getItemFromLine(record);
        assertEquals("1", item.getEmployeeId());
        assertEquals("Google", item.getCompany());
        assertEquals(8, item.getHoursWorked());
        assertEquals(BigDecimal.valueOf(300.0).setScale(2, BigDecimal.ROUND_HALF_EVEN), item.getRatePerHour());
        assertNotNull(item.getDate());
    }

    private CSVRecord getRecord(String csvRecord) throws IOException {
        CSVParser parser = CSVParser.parse(csvRecord, CSVFormat.DEFAULT);
        return parser.getRecords().get(0);
    }

    @Test
    public void getItemFromLine_InvalidColumnCount() throws IOException {
        CSVRecord record = getRecord("1,300,2019-07-01,19:00,17:00");
        try {
            instance.getItemFromLine(record);
            fail("Failure expected");
        } catch (InvalidLineFormatException e) {
            assertEquals(1, e.getLineNumber());
            assertTrue(e.getMessage().contains("count is less"));
        }
    }

    @Test
    public void getItemFromLine_InvalidHours() throws IOException {
        CSVRecord record = getRecord("1,300,Google,2019-07-01,19:00,17:00");
        try {
            instance.getItemFromLine(record);
            fail("Failure expected");
        } catch (InvalidLineFormatException e) {
            assertEquals(1, e.getLineNumber());
            assertTrue(e.getMessage().contains("billable hours"));
        }
    }

    @Test
    public void getItemFromLine_InvalidRate() throws IOException {
        CSVRecord record = getRecord("1,30A0,Google,2019-07-01,11:00,17:00");
        try {
            instance.getItemFromLine(record);
            fail("Failure expected");
        } catch (InvalidLineFormatException e) {
            assertEquals(1, e.getLineNumber());
            assertTrue(e.getMessage().contains("numeric value"));
        }
    }

    @Test
    public void getItemFromLine_InvalidDate() throws IOException {
        CSVRecord record = getRecord("1,300,Google,20190701,11:00,17:00");
        try {
            instance.getItemFromLine(record);
            fail("Failure expected");
        } catch (InvalidLineFormatException e) {
            assertEquals(1, e.getLineNumber());
            assertTrue(e.getMessage().contains("date value"));
        }
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