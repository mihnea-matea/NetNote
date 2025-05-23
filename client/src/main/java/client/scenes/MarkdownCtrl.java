package client.scenes;
import client.LanguageChange;
import client.utils.Config;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Directory;
import commons.Note;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.commonmark.Extension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.*;

public class MarkdownCtrl {
    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private final MainNetNodeCtrl pc;
    private String errorMessageTitle;
    private String errorMessageText;

    private Note currentNote;

    @FXML
    private ListView<Note> noteNameList;

    @FXML
    private ListView<String> fileList;

    private ObservableList<String> fileNames=FXCollections.observableArrayList();
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

    private int charsModifiedSinceLastSave;
    public static final int CHAR_NO_FOR_AUTOSAVE = 3;
    public static final int SECONDS_FOR_AUTOSAVE = 5;

    private Timer autosaveTimer;

    @FXML
    private Button removeButton;

    @FXML
    private Button addFile;

    @FXML
    private ComboBox<String> languageButton;

    @FXML
    private Button addNoteButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button editCollectionsButton;

    private ServerUtils serverUtils = new ServerUtils();

    private boolean autosaveInProgress = false;

    private final List<Extension> extensions = List.of(TablesExtension.create());
    private final Parser parserM = Parser.builder().extensions(extensions).build();
    private final HtmlRenderer rendererH = HtmlRenderer.builder().extensions(extensions).build();

    /**
     * constructor
     *
     * @param p primary controller
     */
    @Inject
    public MarkdownCtrl(MainNetNodeCtrl p, ServerUtils serverUtils) {
        this.pc = p;
        this.serverUtils = serverUtils;
    }

    @FXML
    public void initialize() {
        fileList.setItems(fileNames);
        fileList.setCellFactory(param -> new ListCell<>(){
            private Hyperlink link=new Hyperlink();
            {
                link.setOnAction(event -> {
                    String fileName=getItem();
                    if(fileName!=null){
                        downloadFile(fileName);
                    }
                });
            }
            @Override
            protected void updateItem(String item, boolean empty){
                super.updateItem(item,empty);
                if(empty||item==null){
                    setGraphic(null);
                    setText(null);
                } else{
                    link.setText(item);
                    setGraphic(link);
                }
            }
        });
        markdownText.scrollTopProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                synchronizeScroll(markdownText, htmlText);
            }
        });
        markdownText.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    enterPress();
                }
            }
        });
        renderMarkdownToHTML(markdownTitle, htmlTitle);
        renderMarkdownToHTML(markdownText, htmlText);
        generateMarkdownTitle();
        generateMarkdownText();
        if (noteNameList == null) {
            noteNameList = new ListView<>();
        }
        noteNameList.setItems(notes);

        noteNameList.setCellFactory(param -> new ListCell<>() {
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

        noteNameList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if (oldValue != null && charsModifiedSinceLastSave > 0) {
//                charsModifiedSinceLastSave = 0;
//                oldValue.setTitle(markdownTitle.getText());
//                oldValue.setContent(markdownText.getText());
//                Note updatedOld = serverUtils.updateNote(oldValue);
//                if (updatedOld != null) {
//                    int index = notes.indexOf(updatedOld);
//                    if (index != -1) {
//                        notes.set(index, updatedOld);
//                    }
//                }
//            }
            if (oldValue != null && charsModifiedSinceLastSave > 0) {
                ensureUniqueTitle(oldValue);
                autosaveCertainNote(oldValue);
                //refreshNoteList();
            }

            charsModifiedSinceLastSave = 0;

            if(newValue != null && !autosaveInProgress) {
                currentNote = newValue;

                Note freshNote = serverUtils.getNoteById(newValue.getId());
                if(freshNote != null) {
                    Platform.runLater(() -> {
                        markdownTitle.setText(freshNote.getTitle());
                        markdownText.setText(freshNote.getContent());
                        int index = notes.indexOf(newValue);
                        if (index != -1) {
                            notes.set(index, freshNote);
                        }
                        currentNote = freshNote;
                    });
                } else {
                    Platform.runLater(() -> {
                        markdownTitle.setText(newValue.getTitle());
                        markdownText.setText(newValue.getContent());
                    });
                }
            }
        });

        /*
            The check for control chars was with the help of GPT
         */
        markdownText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (currentNote != null) {
                charsModifiedSinceLastSave++;
                if (charsModifiedSinceLastSave >= CHAR_NO_FOR_AUTOSAVE) {
                    autosaveCurrentNote();
                    charsModifiedSinceLastSave = 0;
                }
            }
        });

        markdownTitle.focusedProperty().addListener((observable, oldFocus, newFocus) -> {
            if (!newFocus) {
                /// currentNote.setTitle(markdownTitle.getText());
                System.out.println("Current title stored in currentNote is: " + currentNote.getTitle());
                ensureUniqueTitle(currentNote);
                System.out.println("Current title stored in currentNote is: " + currentNote.getTitle());
                markdownTitle.setText(currentNote.getTitle());
                currentNote = serverUtils.updateNote(currentNote);
                ///refreshNoteList();
//                ensureUniqueTitle(currentNote);
//                markdownTitle.setText(currentNote.getTitle());
//                refreshNoteList();

            }
        });

        markdownTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            if (currentNote != null) {
                charsModifiedSinceLastSave++;
                if (charsModifiedSinceLastSave >= CHAR_NO_FOR_AUTOSAVE) {
                    autosaveCurrentNote();
                    charsModifiedSinceLastSave = 0;
                }
            }
        });

        searchField.setOnKeyPressed(event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                search();
                event.consume();
            }
        });

        startAutosaveTimer();

        ObservableList<Directory> directories = FXCollections.observableArrayList(serverUtils.getAllDirectories());
        directoryDropDown.setItems(directories);

        directoryDropDown.setCellFactory(comboBox -> new ListCell<Directory>() {
            @Override
            protected void updateItem(Directory directory, boolean empty) {
                super.updateItem(directory, empty);
                if (empty || directory == null) {
                    setText(null);
                } else {
                    setText(directory.getTitle() != null ? directory.getTitle() : "Untitled");
                }
            }
        });

// Set default value
        directoryDropDown.setValue(directories.isEmpty() ? null : directories.get(0));

        directoryDropDown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    List<Note> notes = serverUtils.getDirectoryNotes(newValue);
                    noteNameList.getItems().clear();
                    if (notes != null) {
                        noteNameList.getItems().addAll(notes);
                    } else {
                        System.out.println("Error fetching notes for directories");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Log the error or show user-friendly message
                }
            }
        });

        refreshNoteList();
//            if (newValue == oldValue) {
//                System.out.println("Already selected!");
//            }
//            if(newValue != null) {
//                System.out.println("Directory selected");
//            }


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

        if (languageButton == null) {
            languageButton = new ComboBox<>();
        }

        languageButton.getItems().clear();
        languageButton.getItems().addAll(LanguageChange.getInstance().getText("englishButton"),
                LanguageChange.getInstance().getText("dutchButton"),
                LanguageChange.getInstance().getText("romanianButton"),
                LanguageChange.getInstance().getText("frenchButton"));
        String currentLang = LanguageChange.getInstance().getCurrentLanguage();
        if(currentLang.equals("en"))
            languageButton.getSelectionModel().select(LanguageChange.getInstance().getText("englishButton"));
        else if (currentLang.equals("nl"))
            languageButton.getSelectionModel().select(LanguageChange.getInstance().getText("dutchButton"));
        else if (currentLang.equals("ro"))
            languageButton.getSelectionModel().select(LanguageChange.getInstance().getText("romanianButton"));
        else if (currentLang.equals("fr"))
            languageButton.getSelectionModel().select(LanguageChange.getInstance().getText("frenchButton"));
        else
            languageButton.getSelectionModel().select(LanguageChange.getInstance().getText("englishButton"));

        deleteButton.setTooltip(new Tooltip(LanguageChange.getInstance().getText("tooltip.delete")));
        refreshButton.setTooltip(new Tooltip(LanguageChange.getInstance().getText("tooltip.refresh")));
        addFile.setTooltip(new Tooltip(LanguageChange.getInstance().getText("tooltip.addFile")));
        addFile.setText(LanguageChange.getInstance().getText("button.addFile"));
        editCollectionsButton.setTooltip(new Tooltip(LanguageChange.getInstance().getText(
                "tooltip.editCollectionsButton")));
        editCollectionsButton.setText(LanguageChange.getInstance().getText("button.editCollections"));
        addNoteButton.setTooltip(new Tooltip(LanguageChange.getInstance().getText("tooltip.addFile")));
        searchField.setPromptText(LanguageChange.getInstance().getText("searchBar"));
        searchButton.setText(LanguageChange.getInstance().getText("searchButton"));
        markdownText.setPromptText(LanguageChange.getInstance().getText("noteContent"));
        markdownTitle.setPromptText(LanguageChange.getInstance().getText("noteTitle"));


        languageButton.setCellFactory(comboBox -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String langCode, boolean empty) {
                super.updateItem(langCode, empty);
                if (empty || langCode == null) {
                    setGraphic(null);
                    setText(null);
                }
                else {
                    String oldLangCode = langCode;
                    if(langCode.equals("Engels") || langCode.equals("Engleza") || langCode.equals("Anglais"))
                        langCode = "English";
                    else if(langCode.equals("Nederlands") || langCode.equals("Néerlandais") || langCode.equals("Daneza"))
                        langCode = "Dutch";
                    else if(langCode.equals("Roemeens") || langCode.equals("Roumain") || langCode.equals("Romana"))
                        langCode = "Romanian";
                    else if(langCode.equals("Franceza") || langCode.equals("Frans") || langCode.equals("Francais"))
                        langCode = "French";
                    String path = "/client/flags/" + langCode + ".png";
                    InputStream resourceStream = getClass().getResourceAsStream(path);
                    Image flag = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
                    imageView.setImage(flag);
                    imageView.setFitWidth(24);
                    imageView.setFitHeight(24);
                    setGraphic(imageView);
                    setText(oldLangCode);
                }
            }
        });

        languageButton.setButtonCell(new ListCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String langCode, boolean empty) {
                super.updateItem(langCode, empty);
                if (empty || langCode == null) {
                    setGraphic(null);
                    setText(null);
                }
                else {
                    String oldLangCode = langCode;
                    if(langCode.equals("Engels") || langCode.equals("Engleza") || langCode.equals("Anglais"))
                        langCode = "English";
                    else if(langCode.equals("Nederlands") || langCode.equals("Néerlandais") || langCode.equals("Daneza"))
                        langCode = "Dutch";
                    else if(langCode.equals("Roemeens") || langCode.equals("Roumain") || langCode.equals("Romana"))
                        langCode = "Romanian";
                    else if(langCode.equals("Franceza") || langCode.equals("Frans") || langCode.equals("Francais"))
                        langCode = "French";
                    String path = "/client/flags/" + langCode + ".png";
                    InputStream resourceStream = getClass().getResourceAsStream(path);
                    Image flag = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
                    imageView.setImage(flag);
                    imageView.setFitWidth(24);
                    imageView.setFitHeight(24);
                    setGraphic(imageView);
                    setText(oldLangCode);
                }
            }
        });

    }

    @FXML
    public void onLanguageSwitch(String newLanguage) {
        LanguageChange.getInstance().changeLanguage(newLanguage);
        searchField.setPromptText(LanguageChange.getInstance().getText("searchBar"));
        searchButton.setText(LanguageChange.getInstance().getText("searchButton"));
        markdownText.setPromptText(LanguageChange.getInstance().getText("noteContent"));
        markdownTitle.setPromptText(LanguageChange.getInstance().getText("noteTitle"));

        deleteButton.setTooltip(new Tooltip(LanguageChange.getInstance().getText("tooltip.delete")));
        refreshButton.setTooltip(new Tooltip(LanguageChange.getInstance().getText("tooltip.refresh")));
        addFile.setTooltip(new Tooltip(LanguageChange.getInstance().getText("tooltip.addFile")));
        addFile.setText(LanguageChange.getInstance().getText("button.addFile"));
        addNoteButton.setTooltip(new Tooltip(LanguageChange.getInstance().getText("tooltip.addNoteButton")));
        editCollectionsButton.setTooltip(new Tooltip(LanguageChange.getInstance().getText(
                "tooltip.editCollectionsButton")));
        editCollectionsButton.setText(LanguageChange.getInstance().getText("button.editCollections"));

        languageButton.setOnAction(null);
        languageButton.getItems().clear();
        String english = LanguageChange.getInstance().getText("englishButton");
        String dutch = LanguageChange.getInstance().getText("dutchButton");
        String romanian = LanguageChange.getInstance().getText("romanianButton");
        String french = LanguageChange.getInstance().getText("frenchButton");
        languageButton.getItems().addAll(english, dutch, romanian, french);
        if(LanguageChange.getInstance().getCurrentLanguage().equals("en")) {
            languageButton.getSelectionModel().select(english);
        }
        if(LanguageChange.getInstance().getCurrentLanguage().equals("nl")) {
            languageButton.getSelectionModel().select(dutch);
        }
        if(LanguageChange.getInstance().getCurrentLanguage().equals("ro")) {
            languageButton.getSelectionModel().select(romanian);
        }
        if(LanguageChange.getInstance().getCurrentLanguage().equals("fr")){
            languageButton.getSelectionModel().select(french);
        }
        languageButton.setOnAction(event -> languagePressed());
        pc.getAddNoteCtrl().updateLanguage();
        pc.getEditCollectionsCtrl().updateLanguage();
        pc.getNoteSearchCtrl().updateLanguage();
        Config config = new Config(newLanguage);
        client.utils.ConfigUtils.writeConfig(config);
    }

    @FXML
    private void languagePressed() {
        String selectedOption = languageButton.getSelectionModel().getSelectedItem();
        System.out.println(selectedOption + "test");
        String language = "en";
        System.out.println("Current language is: " + LanguageChange.getInstance().getCurrentLanguage());
        if(LanguageChange.getInstance().getCurrentLanguage().equals("en")) {
            switch (selectedOption) {
                case "English":
                    language = languageEnglish();
                    break;
                case "Dutch":
                    language = languageDutch();
                    break;
                case "Romanian":
                    language = languageRomanian();
                    break;
                case "French":
                    language = languageFrench();
                    break;
            }
        }
        else if(LanguageChange.getInstance().getCurrentLanguage().equals("nl")) {
            switch (selectedOption) {
                case "Engels":
                    language = languageEnglish();
                    break;
                case "Nederlands":
                    language = languageDutch();
                    break;
                case "Roemeens":
                    language = languageRomanian();
                    break;
                case "Frans":
                    language = languageFrench();
                    break;
            }
        }
        else if(LanguageChange.getInstance().getCurrentLanguage().equals("ro")) {
            switch (selectedOption) {
                case "Engleza":
                    language = languageEnglish();
                    break;
                case "Daneza":
                    language = languageDutch();
                    break;
                case "Romana":
                    language = languageRomanian();
                    break;
                case "Franceza":
                    language = languageFrench();
                    break;
            }
        }
        else if(LanguageChange.getInstance().getCurrentLanguage().equals("fr")) {
            switch (selectedOption) {
                case "Anglais":
                    language = languageEnglish();
                    break;
                case "Néerlandais":
                    language = languageDutch();
                    break;
                case "Roumain":
                    language = languageRomanian();
                    break;
                case "Francais":
                    language = languageFrench();
                    break;
            }
        }
        System.out.println(language);
        onLanguageSwitch(language);
    }

    public String languageEnglish() {
        return "en";
    }

    public String languageDutch() {
        return "nl";
    }

    public String languageRomanian() {
        return "ro";
    }

    public String languageFrench() {
        return "fr";
    }


    private boolean isCaretAtTopLine(TextArea textArea) {
        int cursorPosition = textArea.getCaretPosition();
        String textBeforeCaret = textArea.getText(0, cursorPosition);
        return !textBeforeCaret.contains("\n");
    }

    private boolean isCaretAtStart(TextInputControl inputControl) {
        return inputControl.getCaretPosition() == 0;
    }

    private void startAutosaveTimer() {
        Timer autosaveTimer = new Timer(true);
        autosaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(charsModifiedSinceLastSave > 0){
                    charsModifiedSinceLastSave = 0;
                    Platform.runLater(() -> autosaveCurrentNote());
                }
            }
        }, SECONDS_FOR_AUTOSAVE * 1000, SECONDS_FOR_AUTOSAVE * 1000);
    }

    private void stopAutosaveTimer() {
        if (autosaveTimer != null)
            autosaveTimer.cancel();
    }


    public void autosaveCurrentNote() {
        if (autosaveInProgress || currentNote == null || noteNameList.getSelectionModel().getSelectedItem() == null)
            return;
        autosaveInProgress = true;
        Note savedNote = new Note("Hi", "i want to be saved");
        currentNote.setContent(markdownText.getText());
//        currentNote.setTitle(markdownTitle.getText());
        savedNote.setContent(currentNote.getContent());
        savedNote.setTitle(currentNote.getTitle());
        savedNote.setId(currentNote.getId());
        System.out.println("Before "+ currentNote.getContent());
        Note updatedNote = serverUtils.updateNote(savedNote);
        System.out.println("After: "+ updatedNote.getContent());
        if (updatedNote == null)
            System.out.println("Can't autosave note.");
        else {
            int index = notes.indexOf(currentNote);
            if (index != -1) {
                notes.set(index, updatedNote);
                Platform.runLater(() -> noteNameList.refresh());
            }
            currentNote = updatedNote;
            if (!Objects.equals(savedNote.getTitle(), currentNote.getTitle())) {
                Platform.runLater(() -> noteNameList.refresh());
            }
            System.out.println("Note with title " + currentNote.getTitle() + " autosaved locally.");
        }
        autosaveInProgress = false;

    }

    private void autosaveCertainNote(Note note) {
        if (autosaveInProgress || note == null)
            return;
        autosaveInProgress = true;
        note.setTitle(markdownTitle.getText());
        note.setContent(markdownText.getText());

        Note updatedNote = serverUtils.updateNote(note);
        if (updatedNote == null)
            System.out.println("Can't autosave note.");
        else {
            int index = notes.indexOf(currentNote);
            if (index != -1)
                notes.set(index, updatedNote);
            if (!Objects.equals(currentNote.getTitle(), markdownTitle.getText()))
                noteNameList.refresh();
            currentNote = updatedNote;
            System.out.println("Note with title" + currentNote.getTitle() + "autosaved locally.");
        }
        autosaveInProgress = false;
    }

    /**
     * transforms from Markdown format to html
     *
     * @param markdown textArea
     * @param html     webView
     */
    public void renderMarkdownToHTML(TextArea markdown, WebView html) {
        if (markdown != null) {
            markdown.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    if (newValue == null || html == null) {
                        return;
                    }
                    Node doc = parserM.parse(newValue);
                    // loading in table format with the help of ChatGPT
                    String htmlString = """
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

    public void synchronizeScroll(TextArea text, WebView html) {
        double scrollTop = text.getScrollTop();
        double contentHeight = text.getHeight();
        double scrollHeight = text.getScrollTop() + contentHeight;
        double scrollPercentage = scrollTop / scrollHeight;
        //JavaScript command with the help of gpt
        String jsCommand = "window.scrollTo(0, document.body.scrollHeight * " + scrollPercentage + ");";
        html.getEngine().executeScript(jsCommand);
    }

    /**
     * Shows Markdown format for title
     */
    @FXML
    public void generateMarkdownTitle() {
        Node document = new Document();
        Heading heading = new Heading();
        heading.setLevel(2);
        Text content = new Text("# Add a title");
        heading.appendChild(content);
        document.appendChild(heading);
        if (markdownTitle != null) {
            Text text = (Text) (document.getFirstChild().getFirstChild());
            markdownTitle.setText(text.getLiteral());
        } else {
            errorMessageTitle = "MarkdownTitle is null";
        }
    }

    /**
     * Shows Markdown format for text
     */
    @FXML
    public void generateMarkdownText() {
        Node document = new Document();
        Heading heading = new Heading();
        heading.setLevel(2);
        Text content = new Text("""
                # My Note
                This is the content of a note
                ## A Sub section
                You can write **bold** and *italic*""");
        heading.appendChild(content);
        document.appendChild(heading);
        if (markdownText != null) {
            Text text = (Text) (document.getFirstChild().getFirstChild());
            markdownText.setText(text.getLiteral());
        } else {
            errorMessageText = "MarkdownText is null";
        }
    }

    /**
     * sets the caret position after the new text was added
     */
    public void enterPress() {
        String text = markdownText.getText();
        int position = markdownText.getCaretPosition();
        String textAfterEnter = text.substring(0, position);
        textAfterEnter += text.substring(position);
        markdownText.setText(textAfterEnter);
        markdownText.positionCaret(position);

    }

    @FXML
    public void refreshNoteList() {
        List<Note> newNotes = new ArrayList<>();
        if(directoryDropDown.getSelectionModel().getSelectedItem() == null) {
            newNotes = serverUtils.getNotes();
        } else {
            newNotes = serverUtils.getDirectoryNotes(directoryDropDown.getSelectionModel().getSelectedItem());
        }
        ///System.out.println("Refreshed" + newNotes);
        if (newNotes == null) {
            System.out.println("No notes available or server error.");
            newNotes = new ArrayList<>();
        }
        Note selectedNote = noteNameList.getSelectionModel().getSelectedItem();
        List<Note> finalNewNotes = newNotes;
        Platform.runLater(() -> {
            notes.setAll(finalNewNotes);
            if (selectedNote == null)
                return;
            long selectedId = selectedNote.getId();
            Note newNote = serverUtils.getNoteById(selectedId);
            if (finalNewNotes.contains(newNote)) {
                displayNoteContent(newNote);
                displayNoteTitle(newNote);
                noteNameList.getSelectionModel().select(newNote);
            }
        });
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
     *
     * @param note - Selected note
     */
    public void displayNoteContent(Note note) {
        markdownText.setText(note.getContent());
        currentNote.setContent(markdownText.getText());
    }

    /**
     * Sets the main window to show the title of the selected note
     *
     * @param note - Selected note
     */
    public void displayNoteTitle(Note note) {
        markdownTitle.setText(note.getTitle());
        currentNote.setTitle(markdownTitle.getText());
    }

    /**
     * Gets selected note and verifies it exists before displaying it
     */
    @FXML
    private void handleNoteSelection() {
        Note note = noteNameList.getSelectionModel().getSelectedItem();
        currentNote = note;
        if (currentNote != null) {
            displayNoteTitle(note);
            displayNoteContent(note);
        }
    }

    @FXML
    private void handleDirectorySelection() {
        Directory directory = directoryDropDown.getSelectionModel().getSelectedItem();
        if (directory != null) {

        }
    }

    @FXML
    private void search() {
        String filter = searchField.getText().trim();
        if (!filter.isEmpty()) {
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
    public void addButtonPress() {
        pc.showAddScene();
    }

    /**
     * Sets the scene to the EditCollection scene when the button is pressed
     */
    public void editCollectionButtonPress() {
        pc.showEditCollectionScene();
    }

    @FXML
    public void removalWarning() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(LanguageChange.getInstance().getText("dialog.delete.title"));
        dialog.setContentText(LanguageChange.getInstance().getText("dialog.delete.content"));

        ButtonType deleteButtonType = new ButtonType(
                LanguageChange.getInstance().getText("dialog.delete.button.confirm"),
                ButtonBar.ButtonData.OK_DONE
        );
        ButtonType cancelButtonType = new ButtonType(
                LanguageChange.getInstance().getText("dialog.delete.button.cancel"),
                ButtonBar.ButtonData.CANCEL_CLOSE
        );
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, cancelButtonType);

        dialog.getDialogPane().setStyle("-fx-background-color: #fed0bb; -fx-text-fill: black");

        dialog.getDialogPane().lookup(".content").setStyle("-fx-font-size: 18;");
        dialog.getDialogPane().lookupButton(deleteButtonType).setStyle(
                "-fx-background-color: #42f56c; -fx-text-fill: black; -fx-font-size: 18px;");
        dialog.getDialogPane().lookupButton(cancelButtonType).setStyle(
                "-fx-background-color: #F65855; -fx-text-fill: black; -fx-font-size: 18px;");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == deleteButtonType) {
                System.out.println("Note deleted.");
                noteNameList.getSelectionModel().clearSelection();
                long id = currentNote.getId();
                serverUtils.deleteNoteById(id);
                currentNote = null;
                Alert deleted = new Alert(Alert.AlertType.CONFIRMATION);
                deleted.setTitle(LanguageChange.getInstance().getText("delete.confirmation.title"));
                deleted.setHeaderText(LanguageChange.getInstance().getText("delete.confirmation.header"));
                deleted.setContentText(LanguageChange.getInstance().getText("delete.confirmation.content"));
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
     *
     * @return markdownText
     */
    public TextArea getMarkdownText() {
        return markdownText;
    }

    /**
     * setter method for markdownText
     *
     * @param markdownText a TextArea
     */
    public void setMarkdownText(TextArea markdownText) {
        this.markdownText = markdownText;
    }

    /**
     * getter method for errorMessageTitle
     *
     * @return errorMessageTitle
     */
    public String getErrorMessageTitle() {
        return errorMessageTitle;
    }

    /**
     * getter method for errorMessageText
     *
     * @return errorMessageText
     */
    public String getErrorMessageText() {
        return errorMessageText;
    }

    /**
     * getter method for markdownTitle
     *
     * @return markdownTitle
     */
    public TextArea getMarkdownTitle() {
        return markdownTitle;
    }

    /**
     * setter method for markdownTitle
     *
     * @param markdownTitle a TextArea
     */
    public void setMarkdownTitle(TextArea markdownTitle) {
        this.markdownTitle = markdownTitle;
    }

    /**
     * setter method for the note that is currently being edited
     *
     * @param note
     */
    public void setCurrentNote(Note note) {
        this.currentNote = note;
    }

    public Note getCurrentNote() {
        return currentNote;
    }

    public void setServerUtils(ServerUtils serverUtils) {
        this.serverUtils = serverUtils;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public void setSearchField(TextField searchField) {
        this.searchField = searchField;
    }

    @FXML
    public void upload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(addFile.getScene().getWindow());
        if (file == null) {
            errorMessage("Select a file");
            return;
        }
        if (currentNote == null) {
            errorMessage("Select a note before uploading a file");
            return;
        }
        Long noteId = currentNote.getId();
        try{
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String fileUrl = serverUtils.uploadFile(noteId, file.getName(), fileBytes);
            fileNames.add(file.getName());
            String message="File inserted: \n";
            int position = markdownText.getCaretPosition();
            markdownText.insertText(position, message);
            int width=150;
            int height=150;
            String img="![Image]("+file.getName()+")";
            //String img = "<img src=\"" + fileUrl + "\" alt=\"Image\" width=\"150\" height=\"150\">";
            int caretPosition = markdownText.getCaretPosition();
            markdownText.insertText(caretPosition, img);
            htmlText.getEngine().getLoadWorker().stateProperty().addListener((observable,oldValue,newValue) ->{
                if(newValue== Worker.State.SUCCEEDED){
                    htmlText.getEngine().executeScript(
                            //generated with the help of chatGPT
                            "document.querySelectorAll('img').forEach(img => {" +
                                    "   if (img.src.endsWith('" + file.getName() + "')) {" +
                                    "       img.src = '" + fileUrl + "';" +
                                    "       img.style.width='" + width +"px';"+
                                    "       img.style.height='" + height +"px';"+
                                    "   }" +
                                    "});"
                    );
                }
            });
        } catch (Exception e){
            errorMessage("Failed to upload the file"+e.getMessage());
        }

    }

    public void updateNoteInList(Note note) {
        if (note == null) return;
        int index = -1;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == note.getId()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            notes.set(index, note);
            Platform.runLater(() -> noteNameList.refresh());
        }
    }
    private void ensureUniqueTitle(Note note) {
        String baseTitle = markdownTitle.getText();

        // Handle empty title
        if (baseTitle == null || baseTitle.trim().isEmpty()) {
            baseTitle = "Untitled" + note.getId();
            showAlert("Empty Title Detected",
                    "The title cannot be empty!",
                    Alert.AlertType.WARNING);
        }

        String uniqueTitle = generateUniqueTitle(baseTitle, 0, note);
        System.out.println("unique:"+uniqueTitle+" vs base:"+baseTitle);
        // Update title if it's not already the correct one
        if (!uniqueTitle.equals(baseTitle)) {
            showAlert("Duplicate Title Detected",
                    "The title was changed to avoid duplicates!",
                    Alert.AlertType.WARNING);
        }

        if (!markdownTitle.getText().equals(uniqueTitle)) {
            markdownTitle.setText(uniqueTitle);
        }
        note.setTitle(uniqueTitle);
        //currentNote.setTitle(uniqueTitle);
    }



    /**
     * Generates a unique title by appending a number to the base title.
     * If the title already ends with a number, increments it.
     */
    private String generateUniqueTitle(String baseTitle, int count, Note unCheckedNote) {
        String uniqueTitle = (count == 0) ? baseTitle : baseTitle + "(" + count + ")";
        boolean isDuplicate = getNotes().stream()
                .filter(note -> (!note.equals(unCheckedNote) && !(note.getId()==unCheckedNote.getId()))) // Exclude the current note
                .map(Note::getTitle)
                .anyMatch(title -> title.equalsIgnoreCase(uniqueTitle));

        if (isDuplicate) {
            return generateUniqueTitle(baseTitle, count + 1, unCheckedNote);
        }

        return uniqueTitle;
    }

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


    public void errorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
    }

    private void downloadFile(String fileName){
        String fileUrl="http://localhost:8080/files/"+fileName;
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Save file "+fileName);
        fileChooser.setInitialFileName(fileName);
        File fileDestination=fileChooser.showSaveDialog(fileList.getScene().getWindow());
        if(fileDestination==null){
            errorMessage("save place not specified");
            return;
        }
        try(InputStream inputStream=new URL(fileUrl).openStream()){
            Files.copy(inputStream,fileDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            errorMessage("Failed download the file");
        }
    }
    public void setNoteNameList (ListView < Note > noteNameList) {
        this.noteNameList = noteNameList;
    }

    public void setDirectoryDropDown (ComboBox < Directory > directoryDropDown) {
        this.directoryDropDown = directoryDropDown;
    }
    public ListView<Note> getNoteNameList () {
        return noteNameList;
    }

    public ServerUtils getServerUtils() {
        return serverUtils;
    }

    public ListView<String> getFileList() {
        return fileList;
    }

    public void setFileList(ListView<String> fileList) {
        this.fileList = fileList;
    }

    public void setDeleteButton(Button deleteButton){
        this.deleteButton = deleteButton;
    }

    public void setRefreshButton(Button refreshButton){
        this.refreshButton = refreshButton;
    }

    public void setAddFileButton(Button addFileButton) {
        this.addFile = addFileButton;
    }

    public void setAddNoteButton(Button addNoteButton){
        this.addNoteButton = addNoteButton;
    }

    public void setSearchButton(Button searchButton) {
        this.searchButton = searchButton;
    }

    public void setEditCollectionsButton(Button editCollectionsButton) {
        this.editCollectionsButton = editCollectionsButton;
    }

    public ComboBox<Directory> getDirectoryDropDown() {
        return directoryDropDown;
    }
}
