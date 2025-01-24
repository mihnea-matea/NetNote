package server.api;

import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import commons.File;

public class FileControllerTest {
    private FileController fileController;
    private TestFileRepository testFileRepository;
    private TestNoteRepository testNoteRepository;

    @BeforeEach
    public void setUp() {
        testFileRepository = new TestFileRepository();
        testNoteRepository = new TestNoteRepository();

        Note note1 = new Note();
        note1.setTitle("Note 1");
        note1.setContent("this is test note 1");
        note1.setId(1);
        Note note2 = new Note();
        note2.setTitle("Note 2");
        note2.setContent("this is test note 2");
        note2.setId(2);
        testNoteRepository.save(note1);
        testNoteRepository.save(note2);

        File file1 = new File();
        File file2 = new File();
        file1.setNote(note1);
        file2.setNote(note2);
        file1.setFileName("file1");
        file2.setFileName("file2");
        file1.setFileUrl("");
        file2.setFileUrl("");
        file1.setFileType("Image");
        file2.setFileType("Image");
        Long fileSize1 = 111L;
        Long fileSize2 = 222L;
        file1.setFileSize(fileSize1);
        file2.setFileSize(fileSize2);
        testFileRepository.save(file1);
        testFileRepository.save(file2);
    }

    @Test
    void test1(){


    }

    @Test
    void test2(){

    }

    @Test
    void test3(){

    }

    @Test
    void test4(){

    }
}
