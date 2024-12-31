package server.api;

import commons.Directory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.database.DirectoryRepository;
import server.database.NoteRepository;

import java.util.List;

@RestController
@RequestMapping("/api/directories")
public class DirectoryController {

    @Autowired
    private DirectoryRepository directoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping(path = {"", "/"})
    public List<Directory> getAllDirectories() {
        return directoryRepository.findAll();
    }

    @GetMapping("/{id}")
        public ResponseEntity<Directory> getDirectoryById(@PathVariable("id") long id) {
            if (id < 0 || !directoryRepository.existsById(id)) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(directoryRepository.findById(id).get());
    }

    @PostMapping(path = {"", "/"})
        public ResponseEntity<Directory> createDirectory(@RequestBody Directory directory) {
            if (directory.getTitle() == null || directory.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            Directory saved = directoryRepository.save(directory);
            return ResponseEntity.ok(saved);
    }

}
