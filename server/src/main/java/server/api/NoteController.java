package server.api;

import commons.Note;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.NoteRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

//This is just the quote controller but with some small changes!!!!!!!!!!!!!!!!!
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository repo;

    @PersistenceContext
    private EntityManager entityManager;

    public NoteController(NoteRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = { "", "/" })
    public List<Note> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<Note> add(@RequestBody Note note) {
        if (note.getTitle() == null || note.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Note saved = repo.save(note);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Note>> noteSearcher(@RequestParam("filter") String filter) {
        System.out.println(filter);
        if (filter == null || filter.trim().isEmpty()) {return ResponseEntity.badRequest().body(Collections.emptyList());}

        String query = "SELECT n FROM Note n WHERE LOWER(n.content) LIKE LOWER(CONCAT('%', :filter, '%'))";
        List<Note> notes = entityManager.createQuery(query, Note.class).setParameter("filter", filter.trim()).getResultList();
        return ResponseEntity.ok(notes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();

        }
        Note note = repo.findById(id).get();
        repo.delete(note);
        return ResponseEntity.ok().build();
    }

}
