package client.scenes;
import com.google.inject.Inject;
import javafx.scene.Parent ;
import javafx.scene.Scene ;
import javafx.stage.Stage ;
import javafx.util.Pair ;

public class MainNetNodeCtrl{

    @Inject
    private Stage primaryStage;

    private Scene MainScene ;
    public Scene AddScene;

    public void init(Stage primaryStage , Pair <MarkdownCtrl, Parent > mainScene , Pair <AddNoteCtrl, Parent > addNoteScene ){
        this.primaryStage = primaryStage;
        this.MainScene = new Scene(mainScene.getValue());
        this.AddScene = new Scene(addNoteScene.getValue());
        showMainScene();
        primaryStage.show();
    }
    public void showMainScene(){
        primaryStage.setTitle("NetNode");
        primaryStage.setScene(MainScene);
    }
    public void showAddScene() {
        if (primaryStage == null) {
            System.err.println("Error: primaryStage is null");
            return;
        }
        primaryStage.setTitle( "NetNode: add note" );
        if (AddScene == null) {
            System.err.println("Error: AddScene is null");
            return;
        }
        primaryStage.setScene(AddScene);
    }
}