package client.scenes;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Directory;
import commons.Note;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.commonmark.Extension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MarkdownCtrl{
    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private final MainNetNodeCtrl pc;
    private String errorMessageTitle;
    private String errorMessageText;

    private Note currentNote;

    @FXML
    private ListView<Note> noteNameList;

    @FXML
    private ComboBox<Directory> directoryDropDown;

    @FXML
    private TextArea markdownTitle;

    @FXML
    private WebView htmlText;

    @FXML
    private WebView htmlTitle;

    @FXML
    private TextArea markdownText;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    private Note currentlyEditedNote;

    private int charsModifiedSinceLastSave;
    private static final int CHAR_NO_FOR_AUTOSAVE = 5;

    @FXML
    private Button removeButton;

    @FXML
    private Button addFile;


    private ServerUtils serverUtils = new ServerUtils();

    private final List<Extension> extensions = List.of(TablesExtension.create());
    private final Parser parserM = Parser.builder().extensions(extensions).build();
    private final HtmlRenderer rendererH = HtmlRenderer.builder().extensions(extensions).build();

    /**
     * constructor
     * @param p primary controller
     */
    @Inject
    public MarkdownCtrl(MainNetNodeCtrl p, ServerUtils serverUtils) {
        this.pc = p;
        this.serverUtils = serverUtils;
    }

    @FXML
    public void initialize(){
        markdownTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            if(currentlyEditedNote != null){
                currentlyEditedNote.setTitle(newValue);
                serverUtils.updateNote(currentlyEditedNote);
                notes.set(notes.indexOf(currentlyEditedNote), currentlyEditedNote);
                noteNameList.refresh();
                autosaveCurrentNote();
            }
        }
        );
        markdownText.scrollTopProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                synchronizeScroll(markdownText,htmlText);
            }
        });
        markdownText.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode()== KeyCode.ENTER){
                    enterPress();
                }
            }
        });
        renderMarkdownToHTML(markdownTitle, htmlTitle);
        renderMarkdownToHTML(markdownText, htmlText);
        generateMarkdownTitle();
        generateMarkdownText();
        if(noteNameList==null){
            noteNameList=new ListView<>();
        }
        refreshNoteList();
        noteNameList.setItems(notes);

        noteNameList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if(empty || note == null || note.getTitle() == null){
                    setText(null);
                } else {
                    setText(note.getTitle());
                }
            }
        });

        noteNameList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                currentlyEditedNote = newValue;
                displayNoteTitle(newValue);
                displayNoteContent(newValue);
                charsModifiedSinceLastSave = 0;
            }
        });


//KEYBOARD SHORTCUTS AND CHECKS-------------------------------------------------------------------------------
        /**
            The check for control chars was with the help of GPT
         **/
        markdownText.addEventFilter(KeyEvent.KEY_TYPED,event -> {
            if(currentlyEditedNote != null){
                String ch = event.getCharacter();
                if(!ch.isEmpty()) {
                    char charTyped = ch.charAt(0);
                    if(charTyped == '\b' || charTyped == '\t' || charTyped == '\n' || !Character.isISOControl(charTyped)){
                        charsModifiedSinceLastSave++;
                        if (charsModifiedSinceLastSave >= CHAR_NO_FOR_AUTOSAVE){
                            autosaveCurrentNote();
                            charsModifiedSinceLastSave = 0;
                        }
                    }
                    else event.consume();
                }
            }
        });

        markdownTitle.addEventFilter(KeyEvent.KEY_TYPED,event -> {
            if(currentlyEditedNote != null){
                String ch = event.getCharacter();
                if(!ch.isEmpty()) {
                    char charTyped = ch.charAt(0);
                    if(charTyped == '\b' || charTyped == '\t' || charTyped == '\n' || !Character.isISOControl(charTyped)){
                        charsModifiedSinceLastSave++;
                        if (charsModifiedSinceLastSave >= CHAR_NO_FOR_AUTOSAVE){
                            autosaveCurrentNote();
                            charsModifiedSinceLastSave = 0;
                        }
                    }
                    else event.consume();
                }
            }
        });

        searchField.setOnKeyPressed(event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                search();
                event.consume();
            }
        });


            ObservableList<Directory> directories = FXCollections.observableArrayList(serverUtils.getAllDirectories());
            directoryDropDown.setItems(directories);

            directoryDropDown.setCellFactory(comboBox -> new ListCell<Directory>() {
                @Override
                protected void updateItem(Directory directory, boolean empty) {
                    super.updateItem(directory, empty);
                    if(empty || directory == null){
                        setText(null);
                    } else {
                        setText(directory.getTitle());
                    }
                }
            });

        directoryDropDown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue != null) {
//                try {
//                    List<Note> notes = serverUtils.getDirectoryNotes(newValue);
//                    noteNameList.getItems().clear();
//                    if (notes != null) {
//                        noteNameList.getItems().addAll(notes);
//                    } else {
//                        System.out.println("Error fetching notes for directories");
//                    }
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
            if (newValue == oldValue) {
                System.out.println("Already selected!");
            }
            if(newValue != null) {
                System.out.println("Directory selected");
            }
        });


        searchField.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        searchField.requestFocus();
                        event.consume();
                    }
                });
            }
        });

        noteNameList.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                searchField.requestFocus();
                event.consume();
            }
        });

        markdownTitle.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN) {
                markdownText.requestFocus();
                event.consume();
            }
        });

        markdownText.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP && isCaretAtTopLine(markdownText)) {
                markdownTitle.requestFocus();
                event.consume();
            }

        });
        markdownTitle.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.LEFT && isCaretAtStart(markdownTitle)) {
                noteNameList.requestFocus();
                event.consume();
            }
        });

        markdownText.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.LEFT && isCaretAtStart(markdownText)) {
                noteNameList.requestFocus();
                event.consume();
            }
        });

        searchField.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN) {
                markdownTitle.requestFocus();
                event.consume();
            }
        });

        markdownTitle.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP) {
                searchField.requestFocus();
                event.consume();
            }
        });
        markdownText.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                autosaveCurrentNote();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                addButtonPress();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.D) {
                removalWarning();
                event.consume();
            }
        });
        markdownTitle.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                autosaveCurrentNote();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                addButtonPress();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.D) {
                removalWarning();
                event.consume();
            }
        });
        noteNameList.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                autosaveCurrentNote();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                addButtonPress();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.D) {
                removalWarning();
                event.consume();
            }
        });
        searchField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                autosaveCurrentNote();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                addButtonPress();
                event.consume();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.D) {
                removalWarning();
                event.consume();
            }
        });
    }
    private boolean isCaretAtTopLine(TextArea textArea) {
        int cursorPosition = textArea.getCaretPosition();
        String textBeforeCaret = textArea.getText(0, cursorPosition);
        return !textBeforeCaret.contains("\n");
    }

    private boolean isCaretAtStart(TextInputControl inputControl) {
        return inputControl.getCaretPosition() == 0;
    }



    private void autosaveCurrentNote(){
        if(currentlyEditedNote == null)
            return;
        System.out.println("Saving note with title: " + markdownTitle.getText());
        currentlyEditedNote.setTitle(markdownTitle.getText());
        currentlyEditedNote.setContent(markdownText.getText());

        Note updatedNote = serverUtils.updateNote(currentlyEditedNote);
        if(updatedNote == null)
            System.out.println("Can't autosave note.");
        else {
            System.out.println("Note autosaved.");
        }
    }

    /**
     * transforms from Markdown format to html
     * @param markdown textArea
     * @param html webView
     */
    public void renderMarkdownToHTML(TextArea markdown, WebView html) {
        if(markdown !=null){
            markdown.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    if(newValue==null|| html ==null) {
                        return;
                    }
                    Node doc= parserM.parse(newValue);
                    // loading in table format with the help of ChatGPT
                    String htmlString= """
                        <html>
                        <head>
                            <style>
                                table {
                                    border-collapse: collapse;
                                }
                                th, td {
                                    border: 1px solid black;
                                    padding: 8px;
                                    text-align: left;
                                }
                            </style>
                        </head>
                        <body>
                        """ + rendererH.render(doc) + """
                        </body>
                        </html>
                        """;
                    html.getEngine().loadContent(htmlString);
                }
            });

        }
    }

    public void synchronizeScroll(TextArea text, WebView html){
        double scrollTop=text.getScrollTop();
        double contentHeight = text.getHeight();
        double scrollHeight=text.getScrollTop()+contentHeight;
        double scrollPercentage=scrollTop/scrollHeight;
        //JavaScript command with the help of gpt
        String jsCommand="window.scrollTo(0, document.body.scrollHeight * "+scrollPercentage + ");";
        html.getEngine().executeScript(jsCommand);
    }

    /**
     * Shows Markdown format for title
     */
    @FXML
    public void generateMarkdownTitle(){
        Node document = new Document();
        Heading heading = new Heading();
        heading.setLevel(2);
        Text content=new Text("# Add a title");
        heading.appendChild(content);
        document.appendChild(heading);
        if(markdownTitle!=null){
            Text text=(Text)(document.getFirstChild().getFirstChild());
            markdownTitle.setText(text.getLiteral());
        }
        else{
            errorMessageTitle="MarkdownTitle is null";
        }
    }

    /**
     * Shows Markdown format for text
     */
    @FXML
    public void generateMarkdownText(){
        Node document = new Document();
        Heading heading = new Heading();
        heading.setLevel(2);
        Text content=new Text("""
                # My Note
                This is the content of a note
                ## A Sub section
                You can write **bold** and *italic*""");
        heading.appendChild(content);
        document.appendChild(heading);
        if(markdownText!=null){
            Text text = (Text)(document.getFirstChild().getFirstChild());
            markdownText.setText(text.getLiteral());
        }
        else{
            errorMessageText="MarkdownText is null";
        }
    }

    /**
     * sets the caret position after the new text was added
     */
    public void enterPress(){
        String text= markdownText.getText();
        int position=markdownText.getCaretPosition();
        String textAfterEnter=text.substring(0, position);
        textAfterEnter+=text.substring(position);
        markdownText.setText(textAfterEnter);
        markdownText.positionCaret(position);

    }
    @FXML
    public void refreshNoteList() {
        List<Note> newNotes = serverUtils.getNotes();
        if (newNotes == null) {
            System.out.println("No notes available or server error.");
            newNotes = new ArrayList<>();
        }
        notes.clear();
        notes.addAll(newNotes);
        noteNameList.refresh();
        System.out.println("Notes in list: " + notes);
    }
//testing methods-------------------------------------------------
    public ObservableList<Note> getNotes() {
        return notes;
    }

    public void clearNoteList() {
        notes.clear();
        refreshNoteList();
    }
    public boolean isNoteDisplayed(Note expectedNote) {
        return markdownTitle.getText().equals(expectedNote.getTitle()) &&
                markdownText.getText().equals(expectedNote.getContent());
    }

//-------------------------------------------------------------------
    /**
     * Sets the main window to show the contents of the selected note
     * @param note - Selected note
     */
    public void displayNoteContent(Note note){
        markdownText.setText(note.getContent());
    }

    /**
     * Sets the main window to show the title of the selected note
     * @param note - Selected note
     */
    public void displayNoteTitle(Note note){
        markdownTitle.setText(note.getTitle());
    }

    /**
     * Gets selected note and verifies it exists before displaying it
     */
    @FXML
    private void handleNoteSelection() {
        Note note = noteNameList.getSelectionModel().getSelectedItem();
        if(note != null){
            displayNoteTitle(note);
            displayNoteContent(note);
            currentNote = note;
        }
    }

    @FXML
    private void handleDirectorySelection() {
        Directory directory = directoryDropDown.getSelectionModel().getSelectedItem();
        if(directory != null){

        }
    }

    @FXML
    private void search(){
        String filter = searchField.getText().trim();
        if(!filter.isEmpty()) {
            List<Note> filteredNotes = serverUtils.getFilteredNotes(filter);

            if (filteredNotes.isEmpty()) {
                System.out.println("No notes found.");
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/client/scenes/noteSearch.fxml"));
                Parent parent = loader.load();
                NoteSearchCtrl noteSearchCtrl = loader.getController();

                noteSearchCtrl.setResult(FXCollections.observableArrayList(filteredNotes), this);

                Stage stage = new Stage();
                stage.setTitle("Search Results");
                stage.setScene(new Scene(parent));
                stage.setResizable(false);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        searchField.setText("");
    }
    /**
     * Sets the scene to the addNote scene when bottom left button is pressed
     */
    public void addButtonPress(){
        pc.showAddScene();
    }

    @FXML
    public void removalWarning(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirmation");
        dialog.setHeaderText("Are you sure you want to delete this note?");

        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, cancelButtonType);

        dialog.getDialogPane().setStyle("-fx-background-color: #B23A48;");

        Label headerLabel = (Label) dialog.getDialogPane().getHeader();
        if (headerLabel != null) {
            headerLabel.setFont(new Font("System", 18));
            headerLabel.setTextFill(Color.DARKRED);
        }
        dialog.getDialogPane().lookupButton(deleteButtonType).setStyle(
                "-fx-background-color: #2fc436; -fx-text-fill: #fed0bb; -fx-font-size: 14px; -fx-font-weight: bold;");
        dialog.getDialogPane().lookupButton(cancelButtonType).setStyle(
                "-fx-background-color: #f44336; -fx-text-fill: #fed0bb; -fx-font-size: 14px; -fx-font-weight: bold;");

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == deleteButtonType) {
                System.out.println("Note deleted.");

                long id = currentNote.getId();
                serverUtils.deleteNoteById(id);

                Alert deleted = new Alert(Alert.AlertType.CONFIRMATION);
                deleted.setTitle("Deletion succesful");
                deleted.setHeaderText("(Current note) deleted");
                deleted.setContentText("This action cannot be undone.");

                deleted.getDialogPane().setStyle("-fx-background-color: #B23A48;");
                deleted.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color:  #B23A48; -fx-text-fill: #fed0bb;");
                deleted.getDialogPane().lookup(".content").setStyle("-fx-text-fill: #fed0bb; -fx-font-size: 14px; -fx-font-family: 'System';");

                Button button = (Button) deleted.getDialogPane().lookupButton(deleted.getDialogPane().getButtonTypes().get(0));
                button.setStyle("-fx-background-color: #ff3300; -fx-text-fill: white; -fx-font-size: 14px;");

                Button deleteButton = (Button) dialog.getDialogPane().lookupButton(deleteButtonType);
                Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
                if (deleteButton != null) {
                    deleteButton.setFont(new Font("System", 14));  // Font for delete button
                }
                if (cancelButton != null) {
                    cancelButton.setFont(new Font("System", 14));  // Font for cancel button
                }

                deleted.showAndWait();

                refreshNoteList();
                System.out.println("Notes refreshed");


            } else if (result.get() == cancelButtonType) {
                System.out.println("Delete action canceled.");
            }
        }
    }


    /**
     * getter method for markdownText
     * @return markdownText
     */
    public TextArea getMarkdownText() {
        return markdownText;
    }

    /**
     * setter method for markdownText
     * @param markdownText a TextArea
     */
    public void setMarkdownText(TextArea markdownText) {
        this.markdownText = markdownText;
    }

    /**
     * getter method for errorMessageTitle
     * @return errorMessageTitle
     */
    public String getErrorMessageTitle() {
        return errorMessageTitle;
    }

    /**
     * getter method for errorMessageText
     * @return errorMessageText
     */
    public String getErrorMessageText() {
        return errorMessageText;
    }

    /**
     * getter method for markdownTitle
     * @return markdownTitle
     */
    public TextArea getMarkdownTitle() {
        return markdownTitle;
    }

    /**
     * setter method for markdownTitle
     * @param markdownTitle a TextArea
     */
    public void setMarkdownTitle(TextArea markdownTitle) {
        this.markdownTitle = markdownTitle;
    }

    /**
     * setter method for the note that is currently being edited
     * @param note
     */
    public void setCurrentlyEditedNote(Note note) {
        this.currentlyEditedNote = note;
    }

    public void setServerUtils(ServerUtils serverUtils){
        this.serverUtils = serverUtils;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public void setSearchField(TextField searchField) {
        this.searchField = searchField;
    }

    @FXML
    public void upload(){
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Select a file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File file=fileChooser.showOpenDialog(addFile.getScene().getWindow());
        if(file!=null){
            String imgURL=file.toURI().toString();
            String imgMarkdownFormat= "![Image](" + imgURL+")";
            int caretPosition=markdownText.getCaretPosition();
            markdownText.insertText(caretPosition,imgMarkdownFormat);
        }
    }
}