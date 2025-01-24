package client.scenes;

import client.LanguageChange;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Directory;
import commons.Note;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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

    @FXML
    private Label editCollectionsLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label serverLabel;

    @FXML
    private Label collectionLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label collectionStatusLabel;

    @FXML
    private Button clearButton;

    @FXML
    private Button MakeDefaultButton;

    @FXML
    private Button CancelButton;

    @FXML
    private Button SaveButton;

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
        updateLanguage();
    }

    public void updateLanguage() {
        editCollectionsLabel.setText(LanguageChange.getInstance().getText("editCollections.label"));
        titleText.setPromptText(LanguageChange.getInstance().getText("editCollections.titleText.prompt"));
        titleLabel.setText(LanguageChange.getInstance().getText("editCollections.label.title"));
        serverText.setPromptText(LanguageChange.getInstance().getText("editCollections.serverText.prompt"));
        serverLabel.setText(LanguageChange.getInstance().getText("editCollections.label.server"));
        collectionText.setPromptText(LanguageChange.getInstance().getText(
                "editCollections.collectionsText.prompt"));
        collectionLabel.setText(LanguageChange.getInstance().getText("editCollections.label.collection"));
        statusLabel.setText(LanguageChange.getInstance().getText("editCollections.label.status"));

        clearButton.setText(LanguageChange.getInstance().getText("editCollections.button.clear"));
        MakeDefaultButton.setText(LanguageChange.getInstance().getText("editCollections.button.makeDefault"));
        CancelButton.setText(LanguageChange.getInstance().getText("editCollections.button.cancel"));
        SaveButton.setText(LanguageChange.getInstance().getText("editCollections.button.save"));
            ///collectionStatusLabel.setText(LanguageChange.getInstance().getText(
           ///     "editCollections.label.actualStatus"));
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

    /**
     * Makes the selected collection the default collection
     */
    public void makeDefault(){
        Optional<Directory> toDefaultCollection = collections
                .stream()
                .filter(x -> x.getCollection().equals(collectionText.getText()))
                .findFirst();
        if(toDefaultCollection.isPresent()){
            Directory defaultCollection = toDefaultCollection.get();
            defaultCollection.setDefault(true);
            Directory saved = serverUtils.makeDirectoryDefault(defaultCollection);

            collections.stream().filter(x -> x.getDefault()).forEach(x -> x.setDefault(false));
            int index = -1;
            for(int i = 0; i < collections.size(); i++){
                if(collections.get(i).getCollection().equals(collectionText.getText())){
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                collections.set(index, saved);
            }
            collectionsList.setItems(FXCollections.observableList(collections));
            collectionsList.refresh();
            collectionsList.getSelectionModel().select(index);
        } else {
            List<Directory> collectionsCopy = new ArrayList(collections);
            Directory defaultCollection = new Directory(titleText.getText(), collectionText.getText());
            defaultCollection.setDefault(true);
            Directory saved = serverUtils.makeDirectoryDefault(defaultCollection);
            collectionsCopy.stream().filter(x -> x.getDefault()).forEach(x -> x.setDefault(false));
            collectionsCopy.add(saved);
            collectionsList.setItems(FXCollections.observableList(collectionsCopy));
            collectionsList.refresh();
            collectionsList.getSelectionModel().select(collections.size() - 1);
        }
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