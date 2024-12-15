package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Note;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AddNoteCtrl{
    @FXML
    private TextField TitleText;

    @FXML
    private TextField ContentsText;

    private final ServerUtils server;
    private final MainNetNodeCtrl pc;

    @Inject
    public AddNoteCtrl(MainNetNodeCtrl pc, ServerUtils server){
        this.server= server;
        this.pc = pc;
    }

    public void apply() {
        String title = TitleText.getText();
        String content = ContentsText.getText();

        if (title == null || title.trim().isEmpty()) {
            showAlert("Error", "Title is required!", Alert.AlertType.ERROR);
            return;
        }

        Note newNote = new Note();
        newNote.setTitle(title);
        newNote.setContent(content);
        //System.out.println("addNote method is being called"); just a testing statement
        try {
            server.addNote(newNote);
            pc.getMarkdownCtrl().refreshNoteList();
            showAlert("Success", "Note added successfully!", Alert.AlertType.INFORMATION);
            pc.showMainScene();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Failed to add note: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void cancel(){
        clearFields();
        pc.showMainScene();
    }

    public void reset(){
        clearFields();
    }

    public void clearFields(){
        try {
            TitleText.clear();
            ContentsText.clear();
        }
        catch(Exception e){
            System.err.println("Fields could not be deleted");
        }
    }
}