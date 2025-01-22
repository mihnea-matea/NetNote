package server.api;

import commons.Note;
import commons.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.DirectoryRepository;
import server.database.NoteRepository;
import server.service.TagService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

//This is just the quote controller but with some small changes!!!!!!!!!!!!!!!!!
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository repo;
    @Autowired
    private TagService tagService;

    @PersistenceContext
    private EntityManager entityManager;

    private final DirectoryRepository directoryRepository;

    public NoteController(NoteRepository repo, DirectoryRepository directoryRepository) {
        this.repo = repo;
        this.directoryRepository = directoryRepository;
    }

    @GetMapping(path = {"", "/"})
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

    @PostMapping(path = {"", "/"})
    public ResponseEntity<Note> add(@RequestBody Note note) {
        if (note.getTitle() == null || note.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (note.getDirectory().equals("default")) {
            note.setDirectory(directoryRepository.findAll().stream()
                    .filter(x -> x.getDefault() == true)
                    .findFirst().get().getCollection());
            directoryRepository.findAll().stream()
                    .filter(x -> x.getDefault() == true)
                    .forEach(x -> x.addNote(note));
        } else {
            note.setDirectory(directoryRepository.findAll().stream()
                    .filter(x -> x.getCollection().equals(note.getDirectory()))
                    .findFirst().get().getCollection());
            directoryRepository.findAll().stream()
                    .filter(x -> x.getCollection().equals(note.getDirectory()))
                    .forEach(x -> x.addNote(note));
        }
        Note saved = repo.save(note);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Note>> noteSearcher(@RequestParam("filter") String filter) {
        System.out.println(filter);
        if (filter == null || filter.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        String query = "SELECT n FROM Note n WHERE (LOWER(n.content) LIKE LOWER(CONCAT('%', :filter, '%'))) OR (LOWER(n.title) LIKE LOWER(CONCAT('%', :filter, '%')))";
        List<Note> notes = entityManager.createQuery(query, Note.class).setParameter("filter", filter.trim().toLowerCase()).getResultList();
        System.out.println("Filter: " + filter.trim());
        System.out.println("Notes Found: " + notes.size());
        return ResponseEntity.ok(notes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") long id) {
        System.out.println("Received delete request");
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();

        }
        Note note = repo.findById(id).get();
        repo.delete(note);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable("id") long id, @RequestBody Note updatedNote) {
        System.out.println("update request was sent");
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (!repo.existsById(id))
            return ResponseEntity.notFound().build();
        Note existingNote = null;
        Optional<Note> optionalNote = repo.findById(id);
        if (optionalNote.isPresent())
            existingNote = optionalNote.get();

        if (existingNote == null)
            return ResponseEntity.notFound().build();

        existingNote.setContent(updatedNote.getContent());
        existingNote.setTitle(updatedNote.getTitle());
        Note newNote = repo.save(existingNote);
        return ResponseEntity.ok(newNote);
    }

    /**
     * Fetches all tags associated with a note
     * @param noteId ID of the note
     * @return List of tags for the note
     */
    @GetMapping("/{noteId}/tags")
    public ResponseEntity<List<Tag>> getTagsByNote(@PathVariable("noteId") long noteId) {
        try {
            List<Tag> tags = tagService.getTagsByNote(noteId);
            if (tags.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
