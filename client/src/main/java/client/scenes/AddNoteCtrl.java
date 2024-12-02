package client.scenes;

import com.google.inject.Inject;
import client.scenes.MainNetNodeCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddNoteCtrl{
    @FXML
    private TextField TitleText;

    @FXML
    private TextField ContentsText;

    private final MainNetNodeCtrl pc;

    @Inject
    public AddNoteCtrl(MainNetNodeCtrl p){
        this.pc = p;
    }

    public void apply() {
        System.out.println("You added a note" +
                "\nNote will be added to server");
        pc.showMainScene();
        clearFields();
    }

    public void cancel(){
        clearFields();
        pc.showMainScene();
    }

    public void reset(){
        clearFields();
    }

    public void clearFields(){
        try {
            TitleText.clear();
            ContentsText.clear();
        }
        catch(Exception e){
            System.err.println("Fields could not be deleted");
        }
    }
}