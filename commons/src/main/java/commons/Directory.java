package commons;


import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class Directory {
    private String title;
    private ArrayList<Note> notes;
    private static final String SERVER = "http://localhost:8080/";

    /**
     * creates an instance of a Directory
     * @param title
     */
    public Directory(String title) {
        this.title = title;
        this.notes = new ArrayList<>();
        List<Note> allNotes = ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/notes")
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Note>>() {});
        for(int i = 0; i < allNotes.size(); i++){
            if(allNotes.get(i).getTitle().equals(this.title)){
                this.notes.add(allNotes.get(i));
            }
        }
    }

    /**
     * creates an instance of a Directory
     * @return
     */
    public Directory(String title, ArrayList<Note> notes) {
        this.title = title;
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String Title) {
        this.title = Title;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> Notes) {
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
}
