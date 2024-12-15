package client.scenes;
import client.utils.ServerUtils;
import com.google.inject.Inject;
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
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.commonmark.Extension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;
import java.util.List;
public class MarkdownCtrl{

    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private final MainNetNodeCtrl pc;
    private String errorMessageTitle;
    private String errorMessageText;

    @FXML
    private ListView<Note> noteNameList;

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

        /*
            The check for control chars was with the help of GPT
         */
        markdownText.addEventFilter(KeyEvent.KEY_TYPED,event -> {
            if(currentlyEditedNote != null){
                String ch = event.getCharacter();
                if(!ch.isEmpty() && !Character.isISOControl(ch.charAt(0))) {
                    charsModifiedSinceLastSave++;
                    if (charsModifiedSinceLastSave >= CHAR_NO_FOR_AUTOSAVE){
                        autosaveCurrentNote();
                        charsModifiedSinceLastSave = 0;
                    }
                }
            }
        });

        markdownTitle.addEventFilter(KeyEvent.KEY_TYPED,event -> {
            if(currentlyEditedNote != null){
                String ch = event.getCharacter();
                if(!ch.isEmpty() && !Character.isISOControl(ch.charAt(0))) {
                    charsModifiedSinceLastSave++;
                    if (charsModifiedSinceLastSave >= CHAR_NO_FOR_AUTOSAVE){
                        autosaveCurrentNote();
                        charsModifiedSinceLastSave = 0;
                    }
                }
            }
        });
    }

    private void autosaveCurrentNote(){
        if(currentlyEditedNote == null)
            return;
        currentlyEditedNote.setTitle(markdownTitle.getText());
        currentlyEditedNote.setContent(markdownText.getText());

        Note updatedNote = serverUtils.updateNote(currentlyEditedNote);
        if(updatedNote == null)
            System.out.println("Can't autosave note.");
        else {
            currentlyEditedNote = updatedNote;
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
    public void refreshNoteList(){
        List <Note> newNoteList = pc.getNoteOverviewCtrl().loadAndReturnNotes();
        notes.clear();
        notes.addAll(newNoteList);
    }

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("client/scenes/noteSearch.fxml"));
                Parent parent = loader.load();
                NoteSearchCtrl noteSearchCtrl = loader.getController();

                noteSearchCtrl.setResult(filteredNotes, this);

                Stage stage = new Stage();
                stage.setTitle("Search Results");
                stage.setScene(new Scene(parent));
                stage.setResizable(false);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Sets the scene to the addNote scene when bottom left button is pressed
     */
    public void addButtonPress(){
        pc.showAddScene();
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
}