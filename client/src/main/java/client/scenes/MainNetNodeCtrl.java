package client.scenes;
import com.google.inject.Inject;
import javafx.scene.Parent ;
import javafx.scene.Scene ;
import javafx.stage.Stage ;
import javafx.util.Pair ;

public class MainNetNodeCtrl {

    @Inject
    private Stage primaryStage;

    private Scene MainScene;
    private Scene AddScene;
    private Scene EditCollectionScene;
    private MarkdownCtrl markdownCtrl;
// uncomment after adding the collections fxml
//    public void init(Stage primaryStage, Pair<MarkdownCtrl, Parent> mainScene, Pair<AddNoteCtrl,
//            Parent> addNoteScene, Pair<EditCollectionsCtrl, Parent> editCollectionScene, MarkdownCtrl markdownCtrl) {
//        this.primaryStage = primaryStage;
//        this.MainScene = new Scene(mainScene.getValue());
//        this.AddScene = new Scene(addNoteScene.getValue());
//        this.EditCollectionScene = new Scene(editCollectionScene.getValue());
//        this.markdownCtrl = markdownCtrl;
//        showMainScene();
//        primaryStage.show();
//    }

    //comment out after adding the collections fxml
    public void init(Stage primaryStage, Pair<MarkdownCtrl, Parent> mainScene, Pair<AddNoteCtrl,
            Parent> addNoteScene, MarkdownCtrl markdownCtrl) {
        this.primaryStage = primaryStage;
        this.MainScene = new Scene(mainScene.getValue());
        this.AddScene = new Scene(addNoteScene.getValue());
        this.markdownCtrl = markdownCtrl;
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

    public void showEditCollectionScene() {
        if(primaryStage == null || EditCollectionScene == null) {
            System.err.println("Error: primaryStage or EditCollectionScene is null");
        }
        primaryStage.setTitle( "NetNode: edit collection" );
        primaryStage.setScene(EditCollectionScene);
    }

    public MarkdownCtrl getMarkdownCtrl() {
        return this.markdownCtrl;
    }
}