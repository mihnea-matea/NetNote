package client.scenes;

import commons.Note;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NoteSearchCtrl {

    @FXML
    private ListView<String> resultingListView;

    private MarkdownCtrl markdownCtrl;

    /**
     * Initialises the scene
     */
    @FXML
    public void initialize() {
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
    }

    /**
     * Sets the search results in the listview
     * @param searchResults - List of search results
     * @param markdownCtrl - markdown controller
     */
    public void setResult(@NotNull List<Note> searchResults, MarkdownCtrl markdownCtrl) {
        this.markdownCtrl = markdownCtrl;

        resultingListView.getItems().clear();
        for (Note searchResult : searchResults) {
            resultingListView.getItems().add(searchResult.getTitle());
        }

        resultingListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Note note = searchResults.get(resultingListView.getSelectionModel().getSelectedIndex());
                markdownCtrl.setCurrentNote(note);
                markdownCtrl.displayNoteTitle(note);
                markdownCtrl.displayNoteContent(note);
                markdownCtrl.getNoteNameList().getSelectionModel().select(note);
                Stage stage =  (Stage) resultingListView.getScene().getWindow();
                stage.close();
            }
        });
    }

    /**
     * Getter for resulting listView
     * @return - Listview
     */
    public ListView<String> getResultingListView() {
        return resultingListView;
    }

    /**
     * Setter for resulting listView
     * @param resultingListView - Listview
     */
    public void setResultingListView(ListView<String> resultingListView) {
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
