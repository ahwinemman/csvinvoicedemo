package co.petproject.billable.csvservice.controllers;


import co.petproject.billable.csvservice.impl.InvalidLineFormatException;
import co.petproject.billable.csvservice.impl.InvoiceService;
import co.petproject.billable.csvservice.models.CompanyInvoice;
import co.petproject.billable.csvservice.models.ParseRequest;
import co.petproject.billable.csvservice.models.ParseResult;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@RestController
@RequestMapping("/invoice")
@Slf4j
@CrossOrigin
public class InvoiceController {


    @Autowired
    InvoiceService invoiceService;

    //upload CSV
    @PostMapping("/parse")
    public ResponseEntity<Object> parseCsvFile(@RequestBody @Valid ParseRequest request) {
        try (InputStream is = new ByteArrayInputStream(request.getPayload())) {
            return new ResponseEntity<>(invoiceService.parseCsv(is), HttpStatus.OK);
        } catch (InvalidLineFormatException lfe) {
            return new ResponseEntity<>(Collections.singletonMap("error", String.format("%s - Line: %d", lfe.getMessage(), lfe.getLineNumber())), HttpStatus.BAD_REQUEST);
        } catch (IOException ioe) {
            log.error("Could not parse CSV input", ioe);
            return new ResponseEntity<>(Collections.singletonMap("error", "Could not parse input"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //get parse
    @GetMapping("/{invoiceId}")
    public ParseResult getInvoiceById(@PathVariable("invoiceId") String resultId) {
        return invoiceService.getParseResult(resultId);
    }

    @GetMapping("/{invoiceId}/company")
    @ApiResponses({
        @ApiResponse(response = CompanyInvoice.class, code = 200, message = "Company Invoice"),
        @ApiResponse(code = 404, message = "Company or Invoice not found"),
    })
    public ResponseEntity<Object> getCompanyInvoiceFromResult(@PathVariable("invoiceId") String resultId, @RequestParam("companyName") String companyName) {
        CompanyInvoice invoice = invoiceService.getInvoiceItems(resultId, companyName);
        if (invoice != null) {
            return new ResponseEntity<>(invoice, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "The specified company invoice was not found"),
                HttpStatus.NOT_FOUND
            );
        }
    }

}
