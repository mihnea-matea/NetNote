package client.scenes;

import com.google.inject.Inject;
import client.MainNetNodeCtrl;

public class AddNoteCtrl{
    private MainNetNodeCtrl pc;

    @Inject
    public AddNoteCtrl(MainNetNodeCtrl p){
        this.pc = p;
    }
    public void apply() {
        System.out.println(" You added a note");
        pc.showMainScene();
    }

    public void cancel(){
        System.out.println(" You Cancelled");
        pc.showMainScene();
    }

    public void reset(){
        System.out.println(" You reset");
        pc.showMainScene();
    }
}