package server.service;

import commons.Note;
import commons.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.NoteRepository;
import server.database.TagRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private NoteRepository noteRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public TagRepository getTagRepository() {
        return tagRepository;
    }

    public void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Fetches all notes associated with a tag by tag ID
     * @param tagId ID of the tag
     * @return List of notes associated with the tag
     */
    public List<Note> getNotesByTag(Long tagId) {
        Optional<Tag> tagOptional = tagRepository.findById(tagId);
        if (tagOptional.isEmpty()) {
            return List.of();
        }
        return (List<Note>) tagOptional.get().getNotes();
    }

    /**
     * Fetches all tags associated with a note by note ID
     * @param noteId ID of the note
     * @return List of tags associated with the note
     */
    public List<Tag> getTagsByNote(Long noteId) {
        Optional<Note> noteOptional = noteRepository.findById(noteId);
        if (noteOptional.isEmpty()) {
            return List.of();
        }
        return (List<Tag>) noteOptional.get().getTags();
    }
}

