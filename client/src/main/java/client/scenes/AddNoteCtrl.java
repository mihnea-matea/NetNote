package client.scenes;

import client.LanguageChange;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Note;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AddNoteCtrl{
    @FXML
    private TextField titleText;

    @FXML
    private TextField directoryText;

    @FXML
    private Button applyButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button resetButton;

    @FXML
    private Label titleLabel;

    @FXML
    private Label directoryLabel;

    private final ServerUtils server;
    private final MainNetNodeCtrl pc;

    /**
     * Constructor for the scene
     * @param pc the main scene
     * @param server the means of connecting to the server
     */
    @Inject
    public AddNoteCtrl(MainNetNodeCtrl pc, ServerUtils server){
        this.server= server;
        this.pc = pc;
    }

    /**
     * A method that sets the focus on the title field on opening the scene and handles the keyboard shortcuts of the scene
     */
    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() -> titleText.requestFocus());

        titleText.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN) {
                directoryText.requestFocus();
                event.consume();
            }
        });

        directoryText.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP) {
                titleText.requestFocus();
                event.consume();
            }
        });

        titleText.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.A) {
                apply();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                cancel();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.R) {
                reset();
                event.consume();
            }
        });

        directoryText.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.A) {
                apply();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                cancel();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.R) {
                reset();
                event.consume();
            }
        });
        updateLanguage();
    }

    public void updateLanguage() {
        titleText.setPromptText(LanguageChange.getInstance().getText("addNote.titleText.prompt"));
        directoryText.setPromptText(LanguageChange.getInstance().getText("addNote.directoryText.prompt"));
        resetButton.setText(LanguageChange.getInstance().getText("addNote.button.reset"));
        cancelButton.setText(LanguageChange.getInstance().getText("addNote.button.cancel"));
        applyButton.setText(LanguageChange.getInstance().getText("addNote.button.apply"));
        titleLabel.setText(LanguageChange.getInstance().getText("addNote.label.title"));
        directoryLabel.setText(LanguageChange.getInstance().getText("addNote.label.directory"));
    }

    /**
     * The method creates a new note with the specified title and directory and gives it a standard content
     */
    public void apply() {
        String title = titleText.getText();
        String directory = directoryText.getText();

        if (title == null || title.trim().isEmpty()) {
            showAlert("Error", "Title is required!", Alert.AlertType.ERROR);
            return;
        }
        String defaultContent = """
                # My Note
                This is the content of a note
                ## A Sub section
                You can write **bold** and *italic*""";

        Note newNote = new Note();
        newNote.setTitle(title);
        newNote.setContent(defaultContent);
        newNote.setDirectory(directory);
        //System.out.println("addNote method is being called"); just a testing statement
        try {
            server.addNote(newNote);
            pc.getMarkdownCtrl().refreshNoteList();
            showAlert("Success", "Note added successfully!", Alert.AlertType.INFORMATION);
            pc.showMainScene();
            pc.getMarkdownCtrl().refreshNoteList();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "Failed to add note: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * A model of an alert that can be customized for different purposes
     * @param title a general statement like success or error
     * @param message detailed description of the action or problem
     * @param type it can be simply informative, show an error, etc.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                alert.close();
                event.consume();
            }
        });
        alert.showAndWait();
    }

    /**
     * return tot the main scene and abandon the action
     */
    public void cancel(){
        clearFields();
        pc.showMainScene();
    }

    /**
     * I don't even know who made this a two part business
     */
    public void reset(){
        clearFields();
    }

    /**
     * Clear everything written in title or directory
     */
    public void clearFields(){
        try {
            titleText.clear();
            directoryText.clear();
        }
        catch(Exception e){
            System.err.println("Fields could not be deleted");
        }
    }
}