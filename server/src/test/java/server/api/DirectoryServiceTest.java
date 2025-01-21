package server.api;

import commons.Directory;
import commons.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.DirectoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class DirectoryServiceTest {
    private DirectoryService directoryService;
    private TestDirectoryRepository testDirectoryRepository;
    private TestNoteRepository testNoteRepository;

    @BeforeEach
    public void setUp() {
        testDirectoryRepository = new TestDirectoryRepository();
        testNoteRepository = new TestNoteRepository();
        directoryService = new DirectoryService(testDirectoryRepository);
        directoryService.setNoteRepository(testNoteRepository);

        Note note1 = new Note();
        note1.setTitle("Note 1");
        note1.setId(1);
        Note note2 = new Note();
        note2.setTitle("Note 2");
        note2.setId(2);
        testNoteRepository.save(note1);
        testNoteRepository.save(note2);

        Directory directory = new Directory();
        directory.setId(1);
        directory.setTitle("Directory 1");
        directory.setCollection("Directory 1");
        directory.setNotes(new ArrayList<Note>());
        directory.getNotes().add(note1);
        testDirectoryRepository.save(directory);
    }

    @Test
    void fetchNotesByDirectoryTest_AllDirectory() {
        List<Note> notes = directoryService.fetchNotesByDirectory(-1);

        assertNotNull(notes);
        assertEquals(2, notes.size());
        assertEquals("Note 1", notes.getFirst().getTitle());
    }

    @Test
    void fetchNotesByNoteTest() {
        List<Note> notes = directoryService.fetchNotesByDirectory(1);
        assertNotNull(notes);
        assertEquals(1, notes.size());
        assertEquals("Note 1", notes.getFirst().getTitle());
    }
}
