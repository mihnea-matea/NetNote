package client.scenes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import commons.Directory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import client.utils.ServerUtils;
import commons.Note;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;

class MarkdownCtrlTest extends ApplicationTest {

    MainNetNodeCtrl mainNetNode;
    private Note mockNote;
    private MarkdownCtrl markdownCtrl;
    private TextArea markdownTitleArea;
    private TextArea markdownTextArea;
    private WebView html;
    private TextField searchField;
    private int charLimit = MarkdownCtrl.CHAR_NO_FOR_AUTOSAVE;
    private ComboBox<Directory> directoryDropDown;
    private Directory mockDirectory;
    private ObservableList<Directory> mockDirectories;

    @Mock
    private ServerUtils serverUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockNote = new Note();
        mockNote.setTitle("Test Title");
        mockNote.setContent("Test Content");
        serverUtils = mock(ServerUtils.class);
        mainNetNode = new MainNetNodeCtrl();
        when(serverUtils.getNotes()).thenReturn(List.of(new Note(), new Note()));
        markdownCtrl = new MarkdownCtrl(mainNetNode, serverUtils);
        when(serverUtils.updateNote(any(Note.class))).thenReturn(mockNote);
        mainNetNode = new MainNetNodeCtrl();
        markdownCtrl = new MarkdownCtrl(mainNetNode, serverUtils);
        searchField = new TextField();
        markdownCtrl.setSearchField(searchField);
        directoryDropDown = new ComboBox<>();
        markdownCtrl.setDirectoryDropDown(directoryDropDown);
        mockDirectory = new Directory();
        mockDirectory.setTitle("Test Directory");
        mockDirectories = FXCollections.observableArrayList(mockDirectory);
        when(serverUtils.getAllDirectories() ).thenReturn(mockDirectories);

    }

    @Test
    void initialize() {
        markdownTitleArea = new TextArea();
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.initialize();
        assertEquals(markdownCtrl.getMarkdownTitle().getText(), markdownTitleArea.getText());
        assertEquals(markdownCtrl.getMarkdownText().getText(), markdownTextArea.getText());
    }


    @Test
    void generateMarkdownTitleTestTrue() {
        markdownTitleArea = new TextArea();
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.generateMarkdownTitle();
        assertEquals("# Add a title", markdownTitleArea.getText());
    }

    @Test
    void generateMarkdownTitleTestFalse() {
        markdownTitleArea = null;
        markdownCtrl.generateMarkdownTitle();
        assertEquals("MarkdownTitle is null", markdownCtrl.getErrorMessageTitle());
    }

    @Test
    void generateMarkdownTextTestTrue() {
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.generateMarkdownText();
        assertEquals("""
                # My Note
                This is the content of a note
                ## A Sub section
                You can write **bold** and *italic*""", markdownTextArea.getText());
    }

    @Test
    void generateMarkdownTextTestFalse() {
        markdownTextArea = null;
        markdownCtrl.generateMarkdownText();
        assertEquals("MarkdownText is null", markdownCtrl.getErrorMessageText());
    }

    @Test
    void enterPressTest() {
        TextArea textArea = new TextArea();
        markdownCtrl.setMarkdownText(textArea);
        textArea.setText("This is the content of a note");
        textArea.positionCaret(10);
        markdownCtrl.enterPress();
        assertEquals("This is the content of a note", textArea.getText());
        assertEquals(10, textArea.getCaretPosition());
    }

    @Test
    void testAutoSaveAfterDesiredOKCharsText() throws NoSuchFieldException, IllegalAccessException {
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        ListView<Note> noteNameList = new ListView<>();
        markdownCtrl.setNoteNameList(noteNameList);
        markdownCtrl.getNoteNameList().getSelectionModel().select(mockNote);
        markdownCtrl.setCurrentlyEditedNote(mockNote);
        for(int i=0;i<charLimit;i++)
            markdownTextArea.setText(markdownTextArea.getText() + 'a');
        verify(serverUtils, times(1)).updateNote(any(Note.class));
    }

    @Test
    void testMultipleAutosavesText(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        ListView<Note> noteNameList = new ListView<>();
        markdownCtrl.setNoteNameList(noteNameList);
        markdownCtrl.getNoteNameList().getSelectionModel().select(mockNote);
        markdownCtrl.setCurrentlyEditedNote(mockNote);
        for(int i=0;i<charLimit;i++)
            markdownTextArea.setText(markdownTextArea.getText() + 'a');
        verify(serverUtils, times(1)).updateNote(any(Note.class));

        for (int i=0;i<charLimit;i++) {
            markdownTextArea.setText(markdownTextArea.getText() + 'b');
        }
        verify(serverUtils, times(2)).updateNote(any(Note.class));
    }

    @Test
    void testAutoSaveAfterDesiredControlCharsText(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        ListView<Note> noteNameList = new ListView<>();
        markdownCtrl.setNoteNameList(noteNameList);
        markdownCtrl.getNoteNameList().getSelectionModel().select(mockNote);
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<charLimit;i++)
            simulateKeyTypedEvent(markdownTextArea, "\u001B"); /// escape character
        verify(serverUtils, times(0)).updateNote(any(Note.class));
    }

    @Test
    void testAutosaveAfterLessThanDesiredCharsText(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        ListView<Note> noteNameList = new ListView<>();
        markdownCtrl.setNoteNameList(noteNameList);
        markdownCtrl.getNoteNameList().getSelectionModel().select(mockNote);
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<charLimit-1;i++)
            markdownTextArea.setText(markdownTextArea.getText() + 'a');
        verify(serverUtils, times(0)).updateNote(any(Note.class));
    }

    @Test
    void testAutosaveAfterDesiredNewlineCharsText(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        ListView<Note> noteNameList = new ListView<>();
        markdownCtrl.setNoteNameList(noteNameList);
        markdownCtrl.getNoteNameList().getSelectionModel().select(mockNote);
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<charLimit;i++)
            markdownTextArea.setText(markdownTextArea.getText() + '\n');
        verify(serverUtils, times(1)).updateNote(any(Note.class));
    }

    @Test
    void testAutoSaveAfterDesiredOKCharsTitle() throws NoSuchFieldException, IllegalAccessException {
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        ListView<Note> noteNameList = new ListView<>();
        markdownCtrl.setNoteNameList(noteNameList);
        markdownCtrl.getNoteNameList().getSelectionModel().select(mockNote);
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<charLimit;i++)
            markdownTitleArea.setText(markdownTitleArea.getText() + '\n');
        verify(serverUtils, times(1)).updateNote(any(Note.class));
    }

    @Test
    void testMultipleAutosavesTitle(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        ListView<Note> noteNameList = new ListView<>();
        markdownCtrl.setNoteNameList(noteNameList);
        markdownCtrl.getNoteNameList().getSelectionModel().select(mockNote);
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<charLimit;i++)
            markdownTitleArea.setText(markdownTitleArea.getText() + 'a');
        verify(serverUtils, times(1)).updateNote(any(Note.class));

        for (int i=0;i<charLimit;i++) {
            markdownTitleArea.setText(markdownTitleArea.getText() + 'b');
        }
        verify(serverUtils, times(2)).updateNote(any(Note.class));
    }

    @Test
    void testAutoSaveAfterDesiredControlCharsTitle(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<charLimit;i++)
            simulateKeyTypedEvent(markdownTitleArea, "\u001B"); /// escape character
        verify(serverUtils, times(0)).updateNote(any(Note.class));
    }

    @Test
    void testAutosaveAfterLessThanDesiredCharsTitle(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<charLimit-1;i++)
            markdownTitleArea.setText(markdownTitleArea.getText() + 'a');
        verify(serverUtils, times(0)).updateNote(any(Note.class));
    }

    @Test
    void testAutosaveAfterFiveNewlineCharsTitle(){
        searchField = new TextField("Hi");
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        ListView<Note> noteNameList = new ListView<>();
        markdownCtrl.setNoteNameList(noteNameList);
        markdownCtrl.getNoteNameList().getSelectionModel().select(mockNote);
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<charLimit;i++)
            markdownTitleArea.setText(markdownTitleArea.getText() + '\n');
        verify(serverUtils, times(1)).updateNote(any(Note.class));
    }

    /**
     * simulates a key typed event
     * method generated with the help of ChatGPT
     * @param textArea place of generation
     * @param character character to be typed
     */
    private void simulateKeyTypedEvent(TextArea textArea, String character){
        KeyEvent event = new KeyEvent(KeyEvent.KEY_TYPED, character, character, null, false, false, false, false);
        textArea.fireEvent(event);
    }
}
