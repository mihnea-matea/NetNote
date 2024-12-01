package client;
import client.scenes.AddNoteCtrl;
import client.scenes.MarkdownCtrl;
import javafx.scene.Parent ;
import javafx.scene.Scene ;
import javafx.stage.Stage ;
import javafx.util.Pair ;

public class MainNetNodeCtrl{

    private Stage primaryStage;

    private Scene MainScene ;
    private Scene AddScene;

    public void init(Stage primaryStage , Pair <MarkdownCtrl, Parent > mainScene , Pair <AddNoteCtrl, Parent > addNote ){

        this.primaryStage = primaryStage ;
        this.MainScene = new Scene(mainScene.getValue());
        this.AddScene = new Scene( addNote.getValue());
        showMainScene();
        primaryStage.show();
    }
    public void showMainScene() {
        primaryStage.setTitle( "NetNode" );
        primaryStage.setScene(MainScene);
    }
    public void showAddScene() {
        primaryStage.setTitle( "NetNode: add note" );
        primaryStage.setScene(AddScene );
    }
}