package client;

import client.scenes.*;
import client.utils.Config;
import client.utils.ConfigUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import java.net.URL;
import com.google.inject.Injector;
import com.google.inject.Guice;



public class MainClient extends Application {
    private static final Injector INJECTOR = Guice.createInjector( new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Config initialConfig = ConfigUtils.readConfig();
        LanguageChange.getInstance().changeLanguage(initialConfig.getLanguage());
        var mainScene = FXML.load(MarkdownCtrl.class, "client", "scenes", "mainClient.fxml");
        var addScene = FXML.load(AddNoteCtrl.class, "client","scenes", "AddNote.fxml");
        var editCollectionScene = FXML.load(EditCollectionsCtrl.class, "client","scenes", "EditCollection.fxml");
        var noteSearchScene = FXML.load(NoteSearchCtrl.class, "client", "scenes", "noteSearch.fxml");
        var pc = INJECTOR.getInstance(MainNetNodeCtrl.class);
        var markdownCtrl = mainScene.getKey();
        //comment out after adding the collections fxml
//        pc.init(stage, mainScene, addScene, markdownCtrl);
        pc.init(stage, mainScene, addScene, editCollectionScene, noteSearchScene, markdownCtrl);
        stage.setOnCloseRequest(event -> {
            markdownCtrl.autosaveCurrentNote();
        });
        stage.show();
    }

    public static URL getLocation(String path){
        return MainClient.class.getClassLoader().getResource(path);
    }

    public void click(){
        System.out.println("You clicked the button!");
    }
}
