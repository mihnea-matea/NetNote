package client.scenes;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
public class MarkdownCtrl {
    @FXML
    private TextArea markdownTitle;

    @FXML
    private WebView htmlText;

    @FXML
    private WebView htmlTitle;

    @FXML
    private TextArea markdownText;

    private final Parser parserM = Parser.builder().build();
    private final HtmlRenderer rendererH = HtmlRenderer.builder().build();

    public MarkdownCtrl(){}

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
        generateMarkdownTitle();
        generateMarkdownText();
        renderMarkdownToHTML(markdownTitle, htmlTitle);
        renderMarkdownToHTML(markdownText, htmlText);
    }
    private void renderMarkdownToHTML(TextArea markdown, WebView html) {
        if(markdown !=null){
            markdown.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String string, String t1) {
                    if(t1==null|| html ==null){
                        return;
                    }
                    Node doc= parserM.parse(t1);
                    String htmlString= rendererH.render(doc);
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
}