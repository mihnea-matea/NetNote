package commons;


import jakarta.persistence.*;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Entity
public class Directory {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean isDefault;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Note> notes;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public Directory() {}

    /**
     * creates an instance of a Directory
     * @param title
     */
    public Directory(String title) {
        this.title = title;
        this.notes = new ArrayList<>();
        this.isDefault = false;
    }

    /**
     * creates an instance of a Directory
     * @return
     */
    public Directory(String title, List<Note> notes) {
        this.title = title;
        this.notes = notes;
        this.isDefault = false;
    }

    public boolean getIsDefault() {return isDefault;}

    public void setDefault(){this.isDefault = true;}

    public void setNotDefault(){this.isDefault = false;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String Title) {
        this.title = Title;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> Notes) {
        this.notes = Notes;
    }

    public void addNote(Note Note) {
        notes.add(Note);
    }

    public void removeNote(Note Note) {
        notes.remove(Note);
    }

    public boolean containsNote(Note Note) {
        return notes.contains(Note);
    }

    public String toString() {
        return title + "\n" + notes.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directory directory = (Directory) o;
        return Objects.equals(getTitle(), directory.getTitle()) && Objects.equals(getNotes(), directory.getNotes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getNotes());
    }

    /**
     * Sets the currently edited Collection to default, and all other not to default
     */
    public void makeDefault(){
        //Chould be replaced by the real selected collection and allCollections
        Directory selectedCollection = new Directory();
        List<Directory> allCollections = new ArrayList<>();
        if(selectedCollection == null){
            System.out.println("No collection selected or selected is null");

        }
        for(Directory collection : allCollections){
            if(collection.getId() != selectedCollection.getId()){
                collection.setNotDefault();
            }
            if(collection.getId() == selectedCollection.getId()){
                collection.setDefault();
            }
        }
        System.out.println("Make default needs to be implemented");
    }
}
