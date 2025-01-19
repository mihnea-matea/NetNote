package server.service;

import commons.Directory;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.DirectoryRepository;
import server.database.NoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DirectoryService {
    private static final String SERVER = "http://localhost:8080/";

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private NoteRepository noteRepository;

    public DirectoryService(DirectoryRepository directoryRepository) {
        this.directoryRepository = directoryRepository;
    }

    /**
     * Fetches notes by directory
     * @param filter - ID of directory
     * @return - List of notes of directory
     */
    public List<Note> fetchNotesByDirectory(long filter) {
        if (filter == -1) {
            return noteRepository.findAll();
        }
        Optional<Directory> directory = directoryRepository.findById(filter);
        if (directory.isPresent()) {
        List<Note> notes = new ArrayList<>();
        Directory realDirectory = directory.get();
        notes = realDirectory.getNotes();
        return notes;
        } else {
            System.out.println("Directory not found");
            return List.of();
        }
    }

    public DirectoryRepository getDirectoryRepository() {
        return directoryRepository;
    }

    public void setDirectoryRepository(DirectoryRepository directoryRepository) {
        this.directoryRepository = directoryRepository;
    }

    public NoteRepository getNoteRepository() {
        return noteRepository;
    }

    public void setNoteRepository(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }
}
