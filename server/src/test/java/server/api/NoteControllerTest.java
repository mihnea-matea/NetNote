package server.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

import commons.Directory;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class NoteControllerTest {

    private TestDirectoryRepository directoryRepository;
    private TestNoteRepository repo;
    private NoteController sut; // "System Under Test"

    @BeforeEach
    public void setup() {
        repo = new TestNoteRepository();
        directoryRepository = new TestDirectoryRepository();
        sut = new NoteController(repo, directoryRepository);
        Directory allDirectory = new Directory("All", "All");
        allDirectory.setDefault(true);
        directoryRepository.save(allDirectory);
    }

    @Test
    public void getAll_emptyInitially() {
        List<Note> result = sut.getAll();
        assertTrue(result.isEmpty());
        assertTrue(repo.calledMethods.contains("findAll"));
    }

    @Test
    public void cannotAddNoteWithEmptyTitle() {
        Note badNote = new Note("", "Content");
        ResponseEntity<Note> response = sut.add(badNote);

        // The controller returns BAD_REQUEST if title is null or empty
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertFalse(repo.calledMethods.contains("save"));
    }

    @Test
    public void canAddValidNote() {
        Note valid = new Note("Hello", "World", "All");
        ResponseEntity<Note> response = sut.add(valid);

        assertEquals(OK, response.getStatusCode());
        Note created = response.getBody();
        assertNotNull(created);
        assertTrue(created.getId() > 0);
    }

    @Test
    public void getById_nonExistent() {
        ResponseEntity<Note> response = sut.getById(999);
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void getById_existing() {
        Note note = new Note("Title", "Content");
        Note savedNote = repo.save(note);
        ResponseEntity<Note> response = sut.getById(savedNote.getId());
        assertEquals(OK, response.getStatusCode());
        assertEquals("Title", savedNote.getTitle());
    }

    @Test
    public void delete_nonExistent() {
        ResponseEntity<?> response = sut.delete(42);
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void delete_existing() {
        Note note = new Note("Delete me", "some content");
        note.setId(10);
        repo.notes.add(note);

        ResponseEntity<?> response = sut.delete(10);
        assertEquals(OK, response.getStatusCode());
        assertFalse(repo.existsById(10L));
    }

    @Test
    public void updateNote_existing() {
        Note oldNote = new Note("OldTitle", "OldContent");
        Note updatedNote = repo.save(oldNote);

        Note updated = new Note("NewTitle", "NewContent");
        ResponseEntity<?> response = sut.updateNote(oldNote.getId(), updated);
        assertEquals(OK, response.getStatusCode());

        Note returned = (Note) response.getBody();
        assertNotNull(returned);
        assertEquals("NewTitle", updatedNote.getTitle());
    }

    @Test
    public void updateNote_nonExistent() {
        Note updated = new Note("ShouldFail", "No existing ID");
        ResponseEntity<?> response = sut.updateNote(99, updated);
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertFalse(repo.calledMethods.contains("save"));
    }
}
