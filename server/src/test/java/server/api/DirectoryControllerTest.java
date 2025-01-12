package server.api;

import commons.Directory;
import commons.Note;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.ResponseEntity;
import server.database.DirectoryRepository;
import server.database.FileRepository;
import server.database.NoteRepository;
import server.service.DirectoryService;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
    }

    @Test
    void getAllDirectoriesTest() {
        Directory directory = new Directory();
        directory.setId(11);
        directory.setTitle("Test Directory");
        directoryRepository.save(directory);
        ResponseEntity<List<Directory>> response = directoryController.getAllDirectories();

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        assertEquals("All", response.getBody().getFirst().getTitle());
        assertEquals(11, response.getBody().get(1).getId());
    }
}
