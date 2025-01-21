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
import java.util.Optional;

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
        allDirectory.setCollection("All");

        List<Directory> directories = directoryRepository.findAll();
        directories.add(0, allDirectory);
        Optional<Directory> presentDefault = directories.stream().filter(d -> d.getDefault()).findFirst();
        if(!presentDefault.isPresent()) {
            Optional<Directory> existingDefaultCollection = findByCollection("Default");
            Directory newDefaultDirectory;

            if(existingDefaultCollection.isPresent()) {
                newDefaultDirectory = existingDefaultCollection.get();
                newDefaultDirectory.setDefault(true);
            } else {
                newDefaultDirectory = new Directory("Default", "Default");
                newDefaultDirectory.setDefault(true);
            }

            Directory savedDefault = directoryRepository.save(newDefaultDirectory);
            if(!directories.contains(savedDefault)) {
                directories.add(savedDefault);
            }
        }
        return ResponseEntity.ok(directories);
    }

    /**
     * Returns directory by ID
     * @param id - ID of directory
     * @return - directory with matching ID
     */
    @GetMapping("/{id}")
        public ResponseEntity<Directory> getDirectoryById(@PathVariable("id") long id) {
            if (id < -1 || !directoryRepository.existsById(id)) {
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
            if (directoryRepository.existsById(directory.getId())) {
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

    private Optional<Directory> findByCollection(String collection) {
        List<Directory> directories = directoryRepository.findAll();
        return directories.stream().filter(d -> d.getCollection().equals(collection)).findFirst();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Directory> updateDirectory(@PathVariable("id") long id, @RequestBody Directory directory) {
        if (id < -2) {
            return ResponseEntity.badRequest().build();
        }
        if (!directoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Directory existingDirectory = null;
        Optional<Directory> optionalDirectory = directoryRepository.findById(id);
        if (optionalDirectory.isPresent()) {
            existingDirectory = optionalDirectory.get();
            for (Directory directory1 : directoryRepository.findAll()) {
                directory1.setDefault(false);
            }
        }
        if (existingDirectory == null) {
            return ResponseEntity.notFound().build();
        }
        existingDirectory.setTitle(directory.getTitle());
        existingDirectory.setNotes(directory.getNotes());
        existingDirectory.setId(id);
        existingDirectory.setDefault(true);
        existingDirectory.setCollection(directory.getCollection());

        Directory saved = directoryRepository.save(existingDirectory);
        return ResponseEntity.ok(saved);
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
