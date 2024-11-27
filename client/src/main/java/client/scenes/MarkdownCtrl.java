package client.scenes;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownCtrl {
    @FXML
    private TextArea markdownTitle;

    @FXML
    private Label htmlText;

    @FXML
    private Label htmlTitle;

    @FXML
    private TextArea markdownText;

    private final Parser parserM = Parser.builder().build();
    private final HtmlRenderer rendererH = HtmlRenderer.builder().build();

    public MarkdownCtrl(){}

    @FXML
    public void initialize(){
        generateMarkdownTitle();
        generateMarkdownText();
        renderMarkdownToHTML(markdownText, htmlText);
        renderMarkdownToHTML(markdownTitle, htmlTitle);
    }
    private void renderMarkdownToHTML(TextArea markdown, Label html) {
        if(markdown !=null){
            markdown.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String string, String t1) {
                    if(t1==null|| html ==null){
                        return;
                    }
                    Node doc= parserM.parse(t1);
                    String htmlString= rendererH.render(doc);
                    html.setText(htmlString);
                }
            });
        }
    }
    @FXML
    public void generateMarkdownTitle(){
        Node document = new Document();
        Heading heading = new Heading();
        heading.setLevel(2);
        heading.appendChild(new Text("#Add a title"));
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
}