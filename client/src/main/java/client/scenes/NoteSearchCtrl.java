package client.scenes;

import commons.Note;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NoteSearchCtrl {

    @FXML
    ListView<String> resultingListView;

    private MarkdownCtrl markdownCtrl;

    @FXML
    public void initialize() {

    }

    public void setResult(@NotNull List<Note> searchResults, MarkdownCtrl markdownCtrl) {
          this.markdownCtrl = markdownCtrl;

        resultingListView.getItems().clear();
        for (int i = 0; i < searchResults.size(); i++) {
            resultingListView.getItems().add(searchResults.get(i).getTitle());
        }

        resultingListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Note note = searchResults.get(resultingListView.getSelectionModel().getSelectedIndex());
                markdownCtrl.displayNoteTitle(note);
                markdownCtrl.displayNoteContent(note);
                Stage stage =  (Stage) resultingListView.getScene().getWindow();
                stage.close();
            }
        });
    }

}
