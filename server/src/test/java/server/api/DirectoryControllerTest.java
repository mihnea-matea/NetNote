package server.api;

import commons.Directory;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.service.DirectoryService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DirectoryControllerTest {
    private DirectoryController directoryController;
    private DirectoryService directoryService;
    private TestDirectoryRepository directoryRepository;
    private TestNoteRepository noteRepository;

    @BeforeEach
    public void setUp() {
        directoryController = new DirectoryController();
        directoryRepository = new TestDirectoryRepository();
        directoryService = new DirectoryService(directoryRepository);
        noteRepository = new TestNoteRepository();
        directoryController.setDirectoryRepository(directoryRepository);
        directoryController.setNoteRepository(noteRepository);
        directoryController.setDirectoryService(directoryService);


        Note note = new Note();
        note.setId(111);
        note.setTitle("Test Note");
        noteRepository.save(note);

        Directory directory = new Directory();
        directory.setId(11);
        directory.setTitle("Test Directory");
        directory.setCollection("Test Collection");
        directory.setNotes(new ArrayList<>());
        directory.addNote(note);
        directoryRepository.save(directory);
    }

    @Test
    void getAllDirectoriesTest() {
        ResponseEntity<List<Directory>> response = directoryController.getAllDirectories();

        assertNotNull(response);
        assertEquals(3, response.getBody().size());
        assertEquals("Default", response.getBody().get(2).getTitle());
        assertEquals("All", response.getBody().getFirst().getTitle());
        assertEquals(11, response.getBody().get(1).getId());
    }

    @Test
    void getDirectoryByIdTest() {
        ResponseEntity<Directory> response = directoryController.getDirectoryById(11);

        assertNotNull(response);
        assertEquals(11, response.getBody().getId());
    }

    @Test
    void getDirectoryByIdTest_InvalidId() {
        ResponseEntity<Directory> response = directoryController.getDirectoryById(-2);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void createDirectoryTest() {
        ResponseEntity<Directory> response = directoryController.createDirectory(new Directory("New Directory", "All"));

        assertNotNull(response);
        assertEquals("New Directory", response.getBody().getTitle());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getNotesOfDirectoryTest() {
        ResponseEntity<List<Note>> response = directoryController.getNotesOfDirectory("11");

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        assertEquals("Test Note", response.getBody().get(0).getTitle());
    }
}
