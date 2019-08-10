package co.petproject.billable.csvservice.impl;

public class InvalidLineFormatException extends RuntimeException {

    private int lineNumber;

    public InvalidLineFormatException(int lineNumber, String message) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
