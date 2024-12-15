package client.scenes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import client.utils.ServerUtils;
import commons.Note;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

class MarkdownListViewCtrlTest extends ApplicationTest {
    MainNetNodeCtrl mainNetNode;
    private ListView<Note> noteNameList;
    private MarkdownCtrl markdownCtrl;

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
        noteNameList = new ListView<>();
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

