package client.scenes;

import client.utils.ServerUtils;
import commons.Note;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MarkdownCtrlTest extends ApplicationTest {

    MainNetNodeCtrl mainNetNode;
    private ListView<Note> noteNameList;
    private MarkdownCtrl markdownCtrl;
    private TextArea markdownTitleArea;
    private TextArea markdownTextArea;
    private AddNoteCtrl addNoteCtrl;
    private WebView html;

    @Mock
    private ServerUtils serverUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(serverUtils.getNotes()).thenReturn(List.of(
                new Note("Title 1", "Content 1"),
                new Note("Title 2", "Content 2")
        ));
        mainNetNode = new MainNetNodeCtrl();
        markdownCtrl = new MarkdownCtrl(mainNetNode, serverUtils);
        addNoteCtrl = new AddNoteCtrl(mainNetNode, serverUtils);
        noteNameList = new ListView<>();
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
    void testListViewInitialization() {
        noteNameList.setItems(FXCollections.observableArrayList(
                new Note("Title 1", "Content 1"),
                new Note("Title 2", "Content 2")
        ));
        assertEquals(2, noteNameList.getItems().size(), "ListView should have 2 notes");
    }
    @Test
    void testListViewContainsCorrectNotes() {
        noteNameList.setItems(FXCollections.observableArrayList(
                new Note("Title 1", "Content 1"),
                new Note("Title 2", "Content 2")
        ));
        assertTrue(noteNameList.getItems().stream().anyMatch(note -> note.getTitle().equals("Title 1")), "ListView should contain 'Title 1'");
        assertTrue(noteNameList.getItems().stream().anyMatch(note -> note.getTitle().equals("Title 2")), "ListView should contain 'Title 2'");
    }

    @Test
    void testListViewEmptyAfterClear() {
        noteNameList.setItems(FXCollections.observableArrayList(
                new Note("Title 1", "Content 1"),
                new Note("Title 2", "Content 2")
        ));
        noteNameList.getItems().clear();
        assertTrue(noteNameList.getItems().isEmpty(), "ListView should be empty after clearing");
    }
    @Test
    void testListViewUpdateAfterNoteAdded() {
        noteNameList.setItems(FXCollections.observableArrayList(
                new Note("Title 1", "Content 1")
        ));
        noteNameList.getItems().add(new Note("Title 2", "Content 2"));
        assertEquals(2, noteNameList.getItems().size(), "ListView should have 2 notes after adding a new one");
    }

}