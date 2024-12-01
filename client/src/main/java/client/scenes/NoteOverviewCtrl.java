package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Note;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class NoteOverviewCtrl {

    private final ServerUtils server;

    @FXML
    private ListView<String> noteList;

    @Inject
    public NoteOverviewCtrl(ServerUtils server) {
        this.server = server;
    }

    /**
     * Setting up the ListView with all the entries.
     */
    @FXML
    public void initialize() {
        loadNotes();
    }

    /**
     * Get the notes from the server and display them in the ListView.
     */
    public void loadNotes() {
        List<Note> notes = server.getNotes();
        if (notes != null) {
            // Convert notes to a list of strings
            var noteStrings = FXCollections.observableArrayList(
                    notes.stream()
                            .map(note -> note.getTitle())
                            .toList()
            );
            noteList.setItems(noteStrings);
        } else {
            System.out.println("No notes available or server error.");
        }
    }

    /**
     * Add a new note to the ListView and server.
     */
    @FXML
    public void addNote() {
        Note newNote = new Note();
        newNote.setTitle("My Note");
        newNote.setContent("Some content idk");
        Note addedNote = server.addNote(newNote);
        if (addedNote != null) {
            noteList.getItems().add(addedNote.getTitle());
        }
    }
//this is temporary and it should be replaced by a real add once we have the logic for it
}

