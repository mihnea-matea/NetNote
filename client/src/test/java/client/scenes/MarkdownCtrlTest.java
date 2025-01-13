package client.scenes;

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
    void testAutoSaveAfterFiveOKCharsText() throws NoSuchFieldException, IllegalAccessException {
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<5;i++)
            simulateKeyTypedEvent(markdownTextArea, "a");
        verify(serverUtils, times(1)).updateNote(any(Note.class));
    }

    @Test
    void testMultipleAutosavesText(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<5;i++)
            simulateKeyTypedEvent(markdownTextArea, "a");
        verify(serverUtils, times(1)).updateNote(any(Note.class));

        for (int i = 0; i < 5; i++) {
            simulateKeyTypedEvent(markdownTextArea, "b");
        }
        verify(serverUtils, times(2)).updateNote(any(Note.class));
    }

    @Test
    void testAutoSaveAfterFiveControlCharsText(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<5;i++)
            simulateKeyTypedEvent(markdownTextArea, "\u001B"); /// escape character
        verify(serverUtils, times(0)).updateNote(any(Note.class));
    }

    @Test
    void testAutosaveAfterLessThanFiveCharsText(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<3;i++)
            simulateKeyTypedEvent(markdownTextArea, "\n");
        verify(serverUtils, times(0)).updateNote(any(Note.class));
    }

    @Test
    void testAutosaveAfterFiveNewlineCharsText(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<5;i++)
            simulateKeyTypedEvent(markdownTextArea, "\n");
        verify(serverUtils, times(1)).updateNote(any(Note.class));
    }

    @Test
    void testAutoSaveAfterFiveOKCharsTitle() throws NoSuchFieldException, IllegalAccessException {
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<5;i++)
            simulateKeyTypedEvent(markdownTitleArea, "a");
        verify(serverUtils, times(1)).updateNote(any(Note.class));
    }

    @Test
    void testMultipleAutosavesTitle(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<5;i++)
            simulateKeyTypedEvent(markdownTitleArea, "a");
        verify(serverUtils, times(1)).updateNote(any(Note.class));

        for (int i = 0; i < 5; i++) {
            simulateKeyTypedEvent(markdownTitleArea, "b");
        }
        verify(serverUtils, times(2)).updateNote(any(Note.class));
    }

    @Test
    void testAutoSaveAfterFiveControlCharsTitle(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<5;i++)
            simulateKeyTypedEvent(markdownTitleArea, "\u001B"); /// escape character
        verify(serverUtils, times(0)).updateNote(any(Note.class));
    }

    @Test
    void testAutosaveAfterLessThanFiveCharsTitle(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<3;i++)
            simulateKeyTypedEvent(markdownTitleArea, "\n");
        verify(serverUtils, times(0)).updateNote(any(Note.class));
    }

    @Test
    void testAutosaveAfterFiveNewlineCharsTitle(){
        markdownTitleArea = new TextArea();
        markdownTextArea = new TextArea();
        markdownCtrl.setMarkdownText(markdownTextArea);
        markdownCtrl.setMarkdownTitle(markdownTitleArea);
        markdownCtrl.initialize();
        markdownCtrl.setCurrentlyEditedNote(mockNote);

        for(int i=0;i<5;i++)
            simulateKeyTypedEvent(markdownTitleArea, "\n");
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