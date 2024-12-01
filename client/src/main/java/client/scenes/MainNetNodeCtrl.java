package client.scenes;
import commons.Note;
import javafx.scene.Parent ;
import javafx.scene.Scene ;
import javafx.stage.Stage ;
import javafx.util.Pair ;

public class MainNetNodeCtrl{

    private Stage primaryStage;

    private Scene MainScene ;
    private Scene AddScene;

    private NoteOverviewCtrl noteOverviewCtrl;
    private MarkdownCtrl markdownCtrl;

    public void init(Stage primaryStage , Pair <MarkdownCtrl, Parent > mainScene , Pair <AddNoteCtrl, Parent > addNote, NoteOverviewCtrl noteOverviewCtrl, MarkdownCtrl markdownCtrl){

        this.primaryStage = primaryStage ;
        this.MainScene = new Scene(mainScene.getValue());
        this.AddScene = new Scene( addNote.getValue());
        showMainScene();
        primaryStage.show();

        this.noteOverviewCtrl= noteOverviewCtrl;
        this.markdownCtrl = markdownCtrl;
    }
    public void showMainScene() {
        primaryStage.setTitle( "NetNode" );
        primaryStage.setScene(MainScene);
    }
    public void showAddScene() {
        primaryStage.setTitle( "NetNode: add note" );
        primaryStage.setScene(AddScene );
    }

    public NoteOverviewCtrl getNoteOverviewCtrl() {
        return noteOverviewCtrl;
    }
}