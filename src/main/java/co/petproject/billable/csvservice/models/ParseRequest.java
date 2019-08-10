package co.petproject.billable.csvservice.models;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ParseRequest {
    @NotNull
    @NotEmpty
    private byte[] payload;
}
