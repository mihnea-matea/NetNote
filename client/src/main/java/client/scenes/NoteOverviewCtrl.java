package client.scenes;

import client.utils.ServerUtils;
import commons.Note;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class NoteOverviewCtrl {

    private final ServerUtils server;

    @FXML
    private TableView<Note> noteTable;

    @FXML
    private TableColumn<Note, String> titleColumn;

    @FXML
    private TableColumn<Note, String> contentColumn;

    public NoteOverviewCtrl(ServerUtils server) {
        this.server = server;
    }

    /**
     * setting up the table with all the entries
     */
    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitle()));
        contentColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getContent()));

        loadNotes();
    }

    /**
     * get the notes from the table
     */
    public void loadNotes() {
        List<Note> notes = server.getNotes();
        if (notes != null) {
            noteTable.getItems().setAll(notes);
        } else {
            System.out.println("No notes available or server error.");
        }
    }

    /**
     * add a new note to the table
     */
    @FXML
    public void addNote() {
        // placeholder because idk
        Note newNote = new Note();
        newNote.setTitle("my note");
        newNote.setContent("some content idk");
        Note addedNote = server.addNote(newNote);
        noteTable.getItems().add(addedNote);
    }
}
// This probably needs another fxml file for the table view and adding the notes !!!!!!!!!!!!!!!!!

