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
    @Autowired
    private NoteRepository noteRepository;

    /**
     * Fetches all directories in repository
     * @return - List of all directories in repository
     */
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<Directory>> getAllDirectories() {
        Directory allDirectory = new Directory();
        allDirectory.setTitle("All");
        allDirectory.setNotes(noteRepository.findAll());
        allDirectory.setId(-1);

        List<Directory> directories = directoryRepository.findAll();
        directories.add(0, allDirectory);
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
        System.out.println("Directory created: " + directory.getTitle());
        try {
            if (directory.getTitle() == null || directory.getTitle().trim().isEmpty()) {
                System.out.println("Directory title is empty");
                return ResponseEntity.badRequest().build();
            }
            Directory saved = directoryRepository.save(directory);
            System.out.println("Succesfully saved: " + saved.getTitle());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            System.out.println("Error creating directory: " + directory.getTitle());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Gets notes of a directory
     * @param filter - ID of directory
     * @return - list of notes of the directory
     */
    @GetMapping("/search")
    public ResponseEntity<List<Note>> getNotesOfDirectory(@RequestParam("filter") String filter) {
        try{
            List<Note> notes = directoryService.fetchNotesByDirectory(Long.parseLong(filter));
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    public DirectoryService getDirectoryService() {
        return directoryService;
    }

    public void setDirectoryService(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    public DirectoryRepository getDirectoryRepository() {
        return directoryRepository;
    }

    public void setDirectoryRepository(DirectoryRepository directoryRepository) {
        this.directoryRepository = directoryRepository;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public NoteRepository getNoteRepository() {
        return noteRepository;
    }

    public void setNoteRepository(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }
}
