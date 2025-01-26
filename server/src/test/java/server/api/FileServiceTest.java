package server.api;

import commons.File;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileServiceTest {
    private FileProperties fileProperties;
    private FileService fileService;
    private TestFileRepository testFileRepository;
    private TestNoteRepository testNoteRepository;

    @BeforeEach
    void setUp() {
        fileProperties = new FileProperties();
        fileProperties.setUploadDir("Uploads");
        testFileRepository = new TestFileRepository();
        testNoteRepository = new TestNoteRepository();
        fileService = new FileService(fileProperties, testFileRepository, testNoteRepository);

        Note note1 = new Note();
        note1.setTitle("Note 1");
        note1.setId(1L);
        Note note2 = new Note();
        note2.setTitle("Note 2");
        note2.setId(2L);
        testNoteRepository.save(note1);
        testNoteRepository.save(note2);

        Long id1 = 3L;
        File file1 = new File();
        file1.setId(id1);
        file1.setFileName("testFile1");
        file1.setNote(note1);

        Long id2 = 4L;
        File file2 = new File();
        file2.setId(id2);
        file2.setFileName("testFile2");
        file2.setNote(note2);
        testFileRepository.save(file1);
        testFileRepository.save(file2);
    }

    @Test
    void uploadFileAllTest() {
        Long l = -1L;
        List<File> files = fileService.getFilesByNoteId(l);
        assertNotNull(files);
        assertEquals(2, files.size());
        assertEquals("testFile1", files.get(0).getFileName());
        assertEquals(3L, files.get(0).getId());
    }

    @Test
    void uploadFileOneTets() {
        Long k = 2L;
        List<File> files = fileService.getFilesByNoteId(k);
        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("testFile2", files.get(0).getFileName());
        assertEquals(4L, files.get(0).getId());
    }

    @Test
    void deleteFileSuccesTest() {
        List<File> files = fileService.getFilesByNoteId(-1L);
        assertNotNull(files);
        fileService.deleteFile(files.get(0).getId());
        files = fileService.getFilesByNoteId(-1L);
        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("testFile2", files.get(0).getFileName());
        fileService.deleteFile(files.get(0).getId());
        files = fileService.getFilesByNoteId(-1L);
        assertNotNull(files);
        assertEquals(0, files.size());
    }

    @Test
    void deleteFileFailTest() {
        List<File> files = fileService.getFilesByNoteId(-1L);
        assertNotNull(files);
        assertThrows(RuntimeException.class, () -> fileService.deleteFile(10L));
    }

    @Test
    void deleteAllFileTest(){
        List<File> files = fileService.getFilesByNoteId(-1L);
        assertNotNull(files);
        assertThrows(RuntimeException.class, () -> fileService.deleteFile(-1L));
    }

    @Test
    void loadAsResourceSuccesTest() throws Exception {
        Path tempDir = Files.createTempDirectory("testUploads");
        fileProperties.setUploadDir(tempDir.toString());
        Path tempFile = tempDir.resolve("testFile3.txt");
        Files.writeString(tempFile, "Akuna matata");

        Resource resource = fileService.loadAsResource("testFile3.txt");

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
        assertEquals("testFile3.txt", resource.getFilename());
    }

    @Test
    void loadAsResourceNotFoundTest() throws Exception {
        Path tempDir = Files.createTempDirectory("testDirectory");
        fileProperties.setUploadDir(tempDir.toString());

        Exception exception = assertThrows(RuntimeException.class, () -> fileService.loadAsResource("ThisFileDoesNotExists.txt"));
        assertTrue(exception.getMessage().contains("File not found"));
    }

    /**
     * unable to test for this, as resource.isReadable doesnt check whether it is set to false,
     * but acccording to ChatGPT can do something with OS premissions and return true non the less
     */

//    @Test
//    void loadAsResourceNotReadableTest() throws Exception {
//        Path tempDir = Files.createTempDirectory("testDirectory");
//        fileProperties.setUploadDir(tempDir.toString());
//        Path tempFile = tempDir.resolve("testFile4.txt");
//        Files.writeString(tempFile, "akuna matata");
//
//        tempFile.toFile().setReadable(false);
//
//        Exception exception = assertThrows(RuntimeException.class, () -> fileService.loadAsResource("testFile4.txt"));
//        assertTrue(exception.getMessage().contains("File not readable"));
//    }
}
