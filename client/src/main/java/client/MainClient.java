package client;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;

public class MainClient extends Application {
    @FXML
    private TextArea markdownTitle;

    @FXML
    private WebView htmlText;

    @FXML
    private WebView htmlTitle;

    @FXML
    private TextArea markdownText;

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
