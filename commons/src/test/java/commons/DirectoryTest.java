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


        directory1 = new Directory("School", notes1);
        directory2 = new Directory("School", notes1);
        directory3 = new Directory("Free-Time", notes1);
        directory4 = new Directory("School", notes2);
    }

    @Test
    public void testContains(){
        assertTrue(directory1.containsNote(note1));
    }
    @Test
    public void testAddNote(){
        directory1.addNote(note4);
        assertTrue(directory2.containsNote(note4));
    }

    @Test
    public void testRemoveNote(){
        directory1.removeNote(note3);
        assertFalse(directory1.containsNote(note3));
    }

    @Test
    public void testHash(){
        assertEquals(directory1.hashCode(), directory1.hashCode());
        assertEquals(directory1.hashCode(), directory2.hashCode());
        assertNotEquals(directory1.hashCode(), directory3.hashCode());
    }

    @Test
    public void testToString(){
        String test  = "School\n" +
                "[commons.Note@3c9d0b9d[\n" +
                "  content=I know calculus\n" +
                "  id=0\n" +
                "  title=Calculus\n" +
                "], commons.Note@7c469c48[\n" +
                "  content=I dont know calculus\n" +
                "  id=0\n" +
                "  title=Calculus\n" +
                "], commons.Note@12e61fe6[\n" +
                "  content=I like CSEP\n" +
                "  id=0\n" +
                "  title=CSEP\n" +
                "]]";
        assertEquals(test, directory1.toString());
    }

    @Test
    public void testEquals() {
        assertEquals(directory1, directory1);
        assertEquals(directory1, directory2);
        assertNotEquals(directory1, directory3);
    }

}
