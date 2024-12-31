package server.service;

import commons.Note;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;

import java.util.ArrayList;
import java.util.List;

public class DirectoryService {
    private static final String SERVER = "http://localhost:8080/";

    public List<Note> fetchNotesByDirectory(String directory) {
        List<Note> allNotes = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER)
                .path("api/notes")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Note>>() {});

        List<Note> filteredNotes = new ArrayList<>();
        for (Note note : allNotes) {
            if (note.getDirectory().equals(directory)) {
                filteredNotes.add(note);
            }
        }
        return filteredNotes;
    }
}
