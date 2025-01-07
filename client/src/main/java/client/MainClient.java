package client;

import client.scenes.MainNetNodeCtrl;
import client.scenes.AddNoteCtrl;
import client.scenes.MarkdownCtrl;
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
        var mainScene = FXML.load(MarkdownCtrl.class, "client", "scenes", "mainClient.fxml");
        var addScene = FXML.load(AddNoteCtrl.class, "client","scenes", "AddNote.fxml");
        var pc = INJECTOR.getInstance(MainNetNodeCtrl.class);
        var markdownCtrl = mainScene.getKey();
        pc.init(stage, mainScene, addScene, markdownCtrl);
        stage.show();
    }

    public static URL getLocation(String path){
        return MainClient.class.getClassLoader().getResource(path);
    }

    public void click(){
        System.out.println("You clicked the button!");
    }
}
