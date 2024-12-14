package client.scenes;

import client.utils.ServerUtils;
import commons.Note;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MarkdownCtrlTest extends ApplicationTest {

    MainNetNodeCtrl mainNetNode;
    private MarkdownCtrl markdownCtrl;
    private TextArea markdownTitleArea;
    private TextArea markdownTextArea;
    private WebView html;

    @Mock
    private ServerUtils serverUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(serverUtils.getNotes()).thenReturn(List.of(new Note(), new Note()));
        mainNetNode = new MainNetNodeCtrl();
//        NoteOverviewCtrl noteOverviewCtrl = new NoteOverviewCtrl(serverUtils);
//        mainNetNode.setNoteOverviewCtrl(noteOverviewCtrl);
        markdownCtrl = new MarkdownCtrl(mainNetNode);
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

//    @Test
//    void refreshNoteListTestNoChanges() {
//        List<Note> oldNoteList = mainNetNode.getNoteOverviewCtrl().loadAndReturnNotes();
//        markdownCtrl.refreshNoteList();
//        List<Note> newNoteList = mainNetNode.getNoteOverviewCtrl().loadAndReturnNotes();
//        assertEquals(oldNoteList, newNoteList);
//    }
    @Test
    void enterPressTest () {
        TextArea textArea = new TextArea();
        markdownCtrl.setMarkdownText(textArea);
        textArea.setText("This is the content of a note");
        textArea.positionCaret(10);
        markdownCtrl.enterPress();
        assertEquals("This is the content of a note", textArea.getText());
        assertEquals(10, textArea.getCaretPosition());
        }
}