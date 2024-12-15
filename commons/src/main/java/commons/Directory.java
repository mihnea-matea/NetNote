package commons;

import java.util.ArrayList;
import java.util.Objects;

public class Directory {
    private String Title;
    private ArrayList<Note> Notes;

    /**
     * creates an instance of a Directory
     * @param Title
     * @param Notes
     */
    public Directory(String Title, ArrayList<Note> Notes) {
        this.Title = Title;
        this.Notes = Notes;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public ArrayList<Note> getNotes() {
        return Notes;
    }

    public void setNotes(ArrayList<Note> Notes) {
        this.Notes = Notes;
    }

    public void addNote(Note Note) {
        Notes.add(Note);
    }

    public void removeNote(Note Note) {
        Notes.remove(Note);
    }

    public boolean containsNote(Note Note) {
        return Notes.contains(Note);
    }

    public String toString() {
        return Title + "\n" + Notes.toString();
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
