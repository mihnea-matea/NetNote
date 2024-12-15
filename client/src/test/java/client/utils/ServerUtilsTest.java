package client.utils;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;


public class ServerUtilsTest {

    private ServerUtils serverUtils = new ServerUtils();

    @Test
    void testDeleteNoteById_NegativeId() {
        long id = -1;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            serverUtils.deleteNoteById(id);
        });
        assertTrue(exception.getMessage().contains("ID must be a positive number."));
    }

    @Test
    void testDeleteNoteById_NotFound() {
        long id = 1000000000;

        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream errPrintStream = new PrintStream(errStream);
        PrintStream outPrintStream = new PrintStream(outStream);
        System.setErr(errPrintStream);
        System.setOut(outPrintStream);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            serverUtils.deleteNoteById(id);
        });

        assertEquals(404, exception.getStatusCode().value(), "Expected 404 status code");

        String expectedErrMessage = "Error: Note with ID 1000000000 not found";
        assertTrue(errStream.toString().contains(expectedErrMessage),
                "Expected error message not printed to System.err: " + expectedErrMessage);

        String expectedOutMessage = "id is hardcoded to be 10 for now";
        assertTrue(outStream.toString().contains(expectedOutMessage),
                "Expected message not printed to System.out: " + expectedOutMessage);
    }
}