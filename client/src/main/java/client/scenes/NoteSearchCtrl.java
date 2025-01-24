package client.scenes;

import client.LanguageChange;
import commons.Directory;
import commons.Note;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NoteSearchCtrl {

    @FXML
    private ListView<Note> resultingListView;

    @FXML
    private Label noteSearchLabel;

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
        resultingListView.setOnKeyPressed(event -> {
            int selectedIndex = resultingListView.getSelectionModel().getSelectedIndex();
            if (event.getCode() == KeyCode.UP) {
                if (selectedIndex > 0) {
                    resultingListView.getSelectionModel().select(selectedIndex - 1);
                }
            } else if (event.getCode() == KeyCode.DOWN) {
                if (selectedIndex < resultingListView.getItems().size() - 1) {
                    resultingListView.getSelectionModel().select(selectedIndex + 1);
                }
            }
        });

        updateLanguage();
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

        resultingListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (markdownCtrl.getCurrentNote() != null) {
                Note oldNote = markdownCtrl.getCurrentNote();
                oldNote.setTitle(markdownCtrl.getMarkdownTitle().getText());
                oldNote.setContent(markdownCtrl.getMarkdownText().getText());
                Note updatedNote = markdownCtrl.getServerUtils().updateNote(oldNote);

                if (updatedNote != null) {
                    markdownCtrl.updateNoteInList(updatedNote);

                }
            }

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

                Stage stage = (Stage) resultingListView.getScene().getWindow();
                stage.close();
            });
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

    public void updateLanguage() {
        noteSearchLabel.setText(LanguageChange.getInstance().getText("search.label"));
    }
}
