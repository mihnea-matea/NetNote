package server.api;

import commons.File;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileServiceTest {
    private FileProperties fileProperties;
    private FileService fileService;
    private TestFileRepository testFileRepository;
    private TestNoteRepository testNoteRepository;

    @BeforeEach
    void setUp() {
        testFileRepository = new TestFileRepository();
        testNoteRepository = new TestNoteRepository();
        fileService = new FileService(fileProperties,testFileRepository, testNoteRepository);

        Note note1 = new Note();
        note1.setTitle("Note 1");
        note1.setId(1);
        Note note2 = new Note();
        note2.setTitle("Note 2");
        note2.setId(2);
        testNoteRepository.save(note1);
        testNoteRepository.save(note2);

        long id1 = 1;
        File file1 = new File();
        file1.setId(id1);
        file1.setFileName("testFile1");
        file1.setNote(note1);

        long id2 = 2;
        File file2 = new File();
        file2.setId(id2);
        file2.setFileName("testFile2");
        file2.setNote(note2);
        testFileRepository.save(file1);
        testFileRepository.save(file2);
    }

    @Test
    public void uploadFileTest(){
    }

    @Test
    public void getFilesByNoteValidIdTest(){}

    @Test
    public void deleteFileSuccessTest(){}

    @Test
    public void deleteFileFailTest(){}

    @Test
    public void loadAsRecourceTest(){}
}