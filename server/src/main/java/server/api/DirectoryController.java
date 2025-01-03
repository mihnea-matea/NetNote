package server.api;

import commons.Directory;
import commons.Note;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.database.DirectoryRepository;
import server.database.NoteRepository;
import server.service.DirectoryService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/directories")
public class DirectoryController {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private DirectoryRepository directoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetches all directories in repository
     * @return - List of all directories in repository
     */
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<Directory>> getAllDirectories() {
        List<Directory> directories = directoryRepository.findAll();
        return ResponseEntity.ok(directories);
    }

    /**
     * Returns directory by ID
     * @param id - ID of directory
     * @return - directory with matching ID
     */
    @GetMapping("/{id}")
        public ResponseEntity<Directory> getDirectoryById(@PathVariable("id") long id) {
            if (id < 0 || !directoryRepository.existsById(id)) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(directoryRepository.findById(id).get());
    }

    /**
     * Creates and saves a directory
     * @param directory - directory to be created
     * @return - directory
     */
    @PostMapping(path = {"", "/"})
        public ResponseEntity<Directory> createDirectory(@RequestBody Directory directory) {
            if (directory.getTitle() == null || directory.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            Directory saved = directoryRepository.save(directory);
            return ResponseEntity.ok(saved);
    }

    /**
     * Gets notes of a directory
     * @param filter - ID of directory
     * @return - list of notes of the directory
     */
    @GetMapping("/search")
    public ResponseEntity<List<Note>> getNotesOfDirectory(@RequestParam("filter") long filter) {
        try{
            List<Note> notes = directoryService.fetchNotesByDirectory(filter);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
