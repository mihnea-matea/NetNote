package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Directory;
import commons.Note;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;


public class EditCollectionsCtrl {
    private ObservableList<Directory> collections = FXCollections.observableArrayList();

    @FXML
    private TextField titleText;

    @FXML
    private ListView<Directory> collectionsList;

    @FXML
    private TextField serverText;

    @FXML
    private TextField collectionText;

    private final ServerUtils serverUtils;
    private final MainNetNodeCtrl pc;

    /**
     * Constructor for the scene
     * @param pc the main scene
     * @param serverUtils the means of connecting to the server
     */
    @Inject
    public EditCollectionsCtrl(MainNetNodeCtrl pc, ServerUtils serverUtils){
        this.serverUtils= serverUtils;
        this.pc = pc;
    }

    public void initialize(){
        if( collectionsList == null) {
            collectionsList = new ListView<>();
        }
        //refreshCollectionList();
        collectionsList.setItems(collections);

    }

    /**
     * return tot the main scene and abandon the action
     */
    public void cancel(){
        System.out.println("You have pressed cancel");
        clearFields();
        pc.showMainScene();
    }

    /**
     * Clears all textFields
     */
    public void clearFields(){
        System.out.println("Clearing fields");
        try {
            titleText.clear();
            serverText.clear();
            collectionText.clear();
        }
        catch(Exception e){
            System.err.println("Fields could not be cleared");
        }
        System.out.println("Fields cleared");
    }

    /**
     * saves the changes to the currently edited Collection
     */
    public void save(){
        System.out.println("Saving collections");
        System.out.println("Save needs to be implemented");
        System.out.println("Save needs to be implemented");
        System.out.println("Save needs to be implemented");
        clearFields();
        pc.showMainScene();
        System.out.println("Saved collections");
    }


//    @FXML
//    public void refreshCollectionList() {
//        List<Directory> newCollections = serverUtils.getCollections();
//        if (newCollections == null) {
//            System.out.println("No notes available or server error.");
//            newCollections = new ArrayList<>();
//        }
//        Directory selectedCollection = collectionsList.getSelectionModel().getSelectedItem();
//        List<Directory> finalNewCollections = newCollections;
//        Platform.runLater(() -> {
//            collections.setAll(finalNewCollections);
//        });
//    }
}