package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DirectoryTest {
    private Directory directory1;
    private Directory directory2;
    private Directory directory3;
    private Directory directory4;

    private ArrayList<Note> notes1;
    private ArrayList<Note> notes2;

    private Note note1;
    private Note note2;
    private Note note3;
    private Note note4;

    @BeforeEach
    public void setUp() {
        note1 = new Note("Calculus", "I know calculus");
        note2 = new Note("Calculus", "I dont know calculus");
        note3 = new Note("CSEP", "I like CSEP");
        note4 = new Note("CSEP", "I hate CSEP");

        notes1 = new ArrayList<>();
        notes1.add(note1);
        notes1.add(note2);
        notes1.add(note3);

        notes2 = new ArrayList<>();
        notes2.add(note2);
        notes2.add(note3);


        directory1 = new Directory("School", notes1, "All");
        directory2 = new Directory("School", notes1, "All");
        directory3 = new Directory("Free-Time", notes1, "All");
        directory4 = new Directory("School", notes2, "All");

        directory1.setDefault(true);
    }

    @Test
    public void testContainsTrue(){
        assertTrue(directory1.containsNote(note1));
    }

    @Test
    public void testContainsFalse(){
        assertFalse(directory1.containsNote(note4));
    }

    @Test
    public void testAddNoteOne(){
        directory1.addNote(note4);
        assertTrue(directory1.containsNote(note4));
    }

    @Test
    public void testAddNoteMany(){
        directory1.addNote(note1);
        directory1.addNote(note2);
        directory1.addNote(note3);
        directory1.addNote(note4);
        assertTrue(directory1.containsNote(note1));
        assertTrue(directory1.containsNote(note2));
        assertTrue(directory1.containsNote(note3));
        assertTrue(directory1.containsNote(note4));
    }

    @Test
    public void testRemoveNote(){
        directory1.removeNote(note3);
        assertFalse(directory1.containsNote(note3));
    }

    @Test
    public void testRemoveToMany(){
        directory1.removeNote(note1);
        directory1.removeNote(note2);
        directory1.removeNote(note3);
        directory1.removeNote(note4);
        assertFalse(directory1.containsNote(note1));
        assertFalse(directory1.containsNote(note2));
        assertFalse(directory1.containsNote(note3));
    }

    @Test
    public void testHash(){
        assertEquals(directory1.hashCode(), directory1.hashCode());
        assertEquals(directory1.hashCode(), directory2.hashCode());
        assertNotEquals(directory1.hashCode(), directory3.hashCode());
    }

    @Test
    public void testToString1(){
        String test  = "School\n" +
                "[\n" +
                "Calculus\n" +
                "I know calculus\n" +
                ", \n" +
                "Calculus\n" +
                "I dont know calculus\n" +
                ", \n" +
                "CSEP\n" +
                "I like CSEP\n" +
                "]";
        assertEquals(test, directory1.toString());
    }

    @Test
    public void testToString2(){
        String test ="Free-Time\n" +
                "[\n" +
                "Calculus\n" +
                "I know calculus\n" +
                ", \n" +
                "Calculus\n" +
                "I dont know calculus\n" +
                ", \n" +
                "CSEP\n" +
                "I like CSEP\n" +
                "]";
        assertEquals(test, directory3.toString());
    }

    @Test
    public void testToString3(){
        String test ="School\n" +
                "[\n" +
                "Calculus\n" +
                "I dont know calculus\n" +
                ", \n" +
                "CSEP\n" +
                "I like CSEP\n" +
                "]";
        assertEquals(test, directory4.toString());
    }

    @Test
    public void testEquals() {
        assertEquals(directory1, directory1);
        assertEquals(directory1, directory2);
        assertNotEquals(directory1, directory3);
    }

}
