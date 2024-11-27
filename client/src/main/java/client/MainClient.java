package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainClient extends Application {

    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        var fxml=new FXMLLoader();
        fxml.setLocation(getLocation("client/scenes/mainClient.fxml"));
        var scene=new Scene(fxml.load());

        stage.setTitle("NetNote");
        stage.setScene(scene);
        stage.show();
    }

    public static URL getLocation(String path){
        return MainClient.class.getClassLoader().getResource(path);
    }

    public void click(){
        System.out.println("You clicked the button!");
    }
}
