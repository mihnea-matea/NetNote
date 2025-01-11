package server.api;

import commons.Note;
import commons.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.TagRepository;
import server.service.TagService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetches all tags in the repository
     * @return List of all tags
     */
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return ResponseEntity.ok(tags);
    }

    /**
     * Fetches all notes associated with a tag
     * @param tagId ID of the tag
     * @return List of notes for the tag
     */
    @GetMapping("/{tagId}/notes")
    public ResponseEntity<List<Note>> getNotesByTag(@PathVariable("tagId") long tagId) {
        try {
            List<Note> notes = tagService.getNotesByTag(tagId);
            if (notes.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Fetches all tags associated with a note
     * @param noteId ID of the note
     * @return List of tags for the note
     */
    @GetMapping("/notes/{noteId}")
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

    /**
     * Creates a new tag
     * @param tag Tag to be created
     * @return Created tag
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        if (tag.getLabel() == null || tag.getLabel().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Tag savedTag = tagRepository.save(tag);
        return ResponseEntity.ok(savedTag);
    }

    /**
     * Updates an existing tag
     * @param id ID of the tag to update
     * @param updatedTag Tag data to update
     * @return Updated tag
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable("id") long id, @RequestBody Tag updatedTag) {
        Optional<Tag> optionalTag = tagRepository.findById(id);
        if (optionalTag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Tag existingTag = optionalTag.get();
        existingTag.setLabel(updatedTag.getLabel());
        Tag savedTag = tagRepository.save(existingTag);
        return ResponseEntity.ok(savedTag);
    }

    /**
     * Deletes a tag by ID
     * @param id ID of the tag to delete
     * @return Response entity indicating the result
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable("id") long id) {
        if (!tagRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tagRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
