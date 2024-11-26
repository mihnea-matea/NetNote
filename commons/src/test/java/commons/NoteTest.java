package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NoteTest {

    @Test
    public void constructorTest(){
        Note n = new Note("Test Title", "Some content");
        assertEquals("Test Title", n.getTitle());
        assertEquals("Some content", n.getContent());
    }

    @Test
    public void notEqualsTest() throws InterruptedException {
        Note n = new Note("Test Title", "Same content");
        Thread.sleep(1); /// make sure that the notes have different creation times
        Note n1 = new Note("Test Title", "Same content");
        System.out.println(n.getCreationTime());
        System.out.println(n1.getCreationTime());
        assertNotEquals(n, n1);
    }

    @Test
    public void notEqualsHashcodeTest() throws InterruptedException {
        Note n = new Note("Test Title", "Same content");
        Thread.sleep(1); ///Make sure that the notes have different creation times
        Note n1 = new Note("Test Title", "Other content");
        assertNotEquals(n.hashCode(), n1.hashCode());
    }

}
