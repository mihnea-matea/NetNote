package client.scenes;
import java.util.ArrayList;

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

class MarkdownCtrlTest extends ApplicationTest{

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
        NoteOverviewCtrl noteOverviewCtrl = new NoteOverviewCtrl(serverUtils);
        mainNetNode.setNoteOverviewCtrl(noteOverviewCtrl);
        markdownCtrl = new MarkdownCtrl(mainNetNode);
    }

    @Test
    void initialize() {
        markdownTitleArea = new TextArea();
        markdownCtrl.markdownTitle=markdownTitleArea;
        markdownTextArea = new TextArea();
        markdownCtrl.markdownText=markdownTextArea;
        markdownCtrl.initialize();
        assertEquals(markdownCtrl.markdownTitle.getText(), markdownTitleArea.getText());
        assertEquals(markdownCtrl.markdownText.getText(), markdownTextArea.getText());
    }
    

    @Test
    void generateMarkdownTitleTestTrue() {
        markdownTitleArea = new TextArea();
        markdownCtrl.markdownTitle=markdownTitleArea;
        markdownCtrl.generateMarkdownTitle();
        assertEquals("# Add a title",markdownTitleArea.getText());
    }

    @Test
    void generateMarkdownTitleTestFalse() {
        markdownTitleArea = null;
        markdownCtrl.generateMarkdownTitle();
        assertEquals("MarkdownTitle is null",markdownCtrl.errorMessageTitle);
    }
    @Test
    void generateMarkdownTextTestTrue() {
        markdownTextArea = new TextArea();
        markdownCtrl.markdownText=markdownTextArea;
        markdownCtrl.generateMarkdownText();
        assertEquals("""
                # My Note
                This is the content of a note
                ## A Sub section
                You can write **bold** and *italic*""",markdownTextArea.getText());
    }

    @Test
    void generateMarkdownTextTestFalse() {
        markdownTextArea = null;
        markdownCtrl.generateMarkdownText();
        assertEquals("MarkdownText is null",markdownCtrl.errorMessageText);
    }
    @Test
    void enterPress() {
        TextArea textArea = new TextArea();
        markdownCtrl.markdownText=textArea;
        textArea.setText("This is the content of a note");
        markdownCtrl.enterPress();
        assertEquals("This is the content of a note\n",textArea.getText());
        assertEquals(textArea.getLength(),textArea.getCaretPosition());
    }

    @Test
    void refreshNoteListTestNoChanges(){
        List<Note> oldNoteList = mainNetNode.getNoteOverviewCtrl().loadAndReturnNotes();
        markdownCtrl.refreshNoteList();
        List<Note> newNoteList = mainNetNode.getNoteOverviewCtrl().loadAndReturnNotes();
        assertEquals(oldNoteList, newNoteList);
    }
}