package client.scenes;
import client.scenes.MainNetNodeCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Note;
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
public class MarkdownCtrl {

    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private final MainNetNodeCtrl pc;

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

    private ServerUtils serverUtils = new ServerUtils();

    private final List<Extension> extensions = List.of(TablesExtension.create());
    private final Parser parserM = Parser.builder().extensions(extensions).build();
    private final HtmlRenderer rendererH = HtmlRenderer.builder().extensions(extensions).build();

    @Inject
    public MarkdownCtrl(MainNetNodeCtrl p) {
        this.pc = p;
    }

    @FXML
    public void initialize(){
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
                displayNoteTitle(newValue);
                displayNoteContent(newValue);
            }
        });

    }
    private void renderMarkdownToHTML(TextArea markdown, WebView html) {
        if(markdown !=null){
            markdown.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String string, String t1) {
                    if(t1==null|| html ==null) {
                        return;
                    }
                    Node doc= parserM.parse(t1);
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
    }

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
    }
    public void enterPress(){
        String text= markdownText.getText();
        markdownText.setText(text+"\n");
        markdownText.positionCaret(markdownText.getLength());
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

}