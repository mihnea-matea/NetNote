package client.scenes;

import commons.Note;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.util.List;

public class NoteSearchCtrl {

    @FXML
    private ListView<String> resultingListView;

    private List<Note> searchResults;
    private MarkdownCtrl markdownCtrl;

    public void setResult(List<Note> searchResults, MarkdownCtrl markdownCtrl) {
        this.searchResults = searchResults;
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
