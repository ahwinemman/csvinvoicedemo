package co.petproject.billable.csvservice.api;

import co.petproject.billable.csvservice.models.CsvItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface InvoiceSourceReader {

    List<CsvItem> readRecords(InputStream inputStream) throws IOException;

}
