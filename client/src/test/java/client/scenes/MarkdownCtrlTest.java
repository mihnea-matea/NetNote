package client.scenes;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.junit.jupiter.api.Assertions.*;

class MarkdownCtrlTest extends ApplicationTest{
    MainNetNodeCtrl mainNetNode;
    private MarkdownCtrl markdownCtrl;
    private TextArea markdownTitleArea;
    private TextArea markdownTextArea;
    private WebView html;

    @BeforeEach
    void setUp() {
        mainNetNode = new MainNetNodeCtrl();
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
        assertEquals("# Add a title",markdownTitleArea.getText());
    }

    @Test
    void generateMarkdownTitleTestFalse() {
        markdownTitleArea = null;
        markdownCtrl.generateMarkdownTitle();
        assertEquals("MarkdownTitle is null",markdownCtrl.getErrorMessageTitle());
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
                You can write **bold** and *italic*""",markdownTextArea.getText());
    }

    @Test
    void generateMarkdownTextTestFalse() {
        markdownTextArea = null;
        markdownCtrl.generateMarkdownText();
        assertEquals("MarkdownText is null",markdownCtrl.getErrorMessageText());
    }
//    @Test
//    void enterPress() {
//        TextArea textArea = new TextArea();
//        markdownCtrl.setMarkdownText(textArea);
//        textArea.setText("This is the content of a note");
//        markdownCtrl.enterPress();
//        assertEquals("This is the content of a note\n",textArea.getText());
//        assertEquals(textArea.getLength(),textArea.getCaretPosition());
//    }
}