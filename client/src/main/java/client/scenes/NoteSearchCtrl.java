package client.scenes;

import commons.Directory;
import commons.Note;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NoteSearchCtrl {

    @FXML
    private ListView<Note> resultingListView;

    private MarkdownCtrl markdownCtrl;
    /**
     * Initialises the scene
     */
    @FXML
    public void initialize() {
        resultingListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null || note.getTitle() == null) {
                    setText(null);
                } else {
                    setText(note.getTitle());
                }
            }
        });
        resultingListView.setFocusTraversable(true);
        resultingListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Note selectedNote = resultingListView.getSelectionModel().getSelectedItem();
                if (selectedNote != null) {
                    Stage stage = (Stage) resultingListView.getScene().getWindow();
                    stage.close();
                }
            }
        });
    }

    /**
     * Sets the search results in the listview
     * @param searchResults - List of search results
     * @param markdownCtrl - markdown controller
     */
    public void setResult(@NotNull List<Note> searchResults, MarkdownCtrl markdownCtrl) {
        this.markdownCtrl = markdownCtrl;

        resultingListView.getItems().clear();
        resultingListView.getItems().addAll(searchResults);
        Platform.runLater(() -> resultingListView.requestFocus());

        resultingListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            // Only process the new selection when explicitly confirmed
            Note newNote = markdownCtrl.getServerUtils().getNoteById(newValue.getId());
            if (newNote != null) {
                newValue = newNote;
            }

            Note finalNewValue = newValue;
            Platform.runLater(() -> {
                markdownCtrl.setCurrentNote(finalNewValue);
                markdownCtrl.getNoteNameList().getSelectionModel().select(finalNewValue);
                markdownCtrl.displayNoteTitle(finalNewValue);
                markdownCtrl.displayNoteContent(finalNewValue);
            });
        });

        resultingListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Note selectedNote = resultingListView.getSelectionModel().getSelectedItem();
                if (selectedNote != null) {
                    Stage stage = (Stage) resultingListView.getScene().getWindow();
                    stage.close();
                }
            }
        });
    }

    /**
     * Getter for resulting listView
     * @return - Listview
     */
    public ListView<Note> getResultingListView() {
        return resultingListView;
    }

    /**
     * Setter for resulting listView
     * @param resultingListView - Listview
     */
    public void setResultingListView(ListView<Note> resultingListView) {
        this.resultingListView = resultingListView;
    }

    /**
     * Getter for markdown controller
     * @return - Markdown controller
     */
    public MarkdownCtrl getMarkdownCtrl() {
        return markdownCtrl;
    }

    public void setMarkdownCtrl(MarkdownCtrl markdownCtrl) {
        this.markdownCtrl = markdownCtrl;
    }
}
