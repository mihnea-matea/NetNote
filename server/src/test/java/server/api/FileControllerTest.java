package server.api;

import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import commons.File;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class FileControllerTest {
    private FileService fileService;
    private TestNoteRepository testNoteRepository;
    private FileProperties fileProperties;
    private FileController fileController;
    private TestFileRepository testFileRepository;


    @BeforeEach
    void setUp() {
        fileProperties = new FileProperties();
        fileProperties.setUploadDir("Uploads");
        testFileRepository = new TestFileRepository();
        testNoteRepository = new TestNoteRepository();
        fileService = new FileService(fileProperties, testFileRepository, testNoteRepository);
        fileController = new FileController(fileService, testNoteRepository);

        Note note1 = new Note();
        note1.setTitle("Note 1");
        note1.setId(1L);
        Note note2 = new Note();
        note2.setTitle("Note 2");
        note2.setId(2L);
        Note note3 = new Note();
        note3.setTitle("Note 3");
        note3.setId(3L);
        testNoteRepository.save(note1);
        testNoteRepository.save(note2);
        testNoteRepository.save(note3);

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
    void initializedTest(){
        assertNotNull(fileController);
    }

    /**
     * i couldn't figure out how to test this, not by myself, nor when asking friends and chatGPT
     */
//    @Test
//    void listFilesSuccessTest() throws Exception {
//        // Mock data
//        Note note = new Note();
//        note.setId(1L);
//        note.setTitle("Note 1");
//
//        File file = new File();
//        file.setFileName("test.txt");
//        file.setFileUrl("http://example.com/test.txt");
//        file.setFileType("text/plain");
//        file.setFileSize(1024L);
//        file.setNote(note);
//
//        List<File> files = List.of(file);
//
//        assertNotNull(fileService);
//        // Mock service behavior
//        when(fileService.getFilesByNoteId(1L)).thenReturn(files);
//
//        // Perform GET request and verify response
//        mockMvc.perform(get("/api/files/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0]").value("http://localhost/api/files/serve/test.txt"));
//
//        // Verify service method was called
//        verify(fileService, times(1)).getFilesByNoteId(1L);
//    }

    @Test
    void listFilesTestEmpty(){
        assertEquals(ResponseEntity.noContent().build(), fileController.listFiles(3L));
    }

    @Test
    void serveFileNotFoundTest() throws Exception {
        assertEquals(ResponseEntity.notFound().build(), fileController.serveFile("this doesn't exist"));
    }

    @Test
    void uploadFilesTest(){

    }

    /**
     * i couldn't figure out how to test this, not by myself, nor when asking friends and chatGPT
     */
//    @Test
//    void uploadFilesTest_Success() throws Exception {
//        // Step 1: Prepare the test data
//        Long noteId = 1L;
//        Note note = new Note();
//        note.setId(noteId);
//        note.setTitle("Test Note");
//
//        // Mock repository to return the note
//        when(testNoteRepository.findById(noteId)).thenReturn(Optional.of(note));
//
//        // Mock fileService to return a dummy File object
//        File uploadedFile = new File();
//        uploadedFile.setFileName("test.txt");
//        when(fileService.uploadFile(eq(noteId), any(MultipartFile.class))).thenReturn(uploadedFile);
//
//        // Prepare a mock MultipartFile
//        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "This is a test.".getBytes());
//
//        // Step 2: Perform the file upload request
//        mockMvc.perform(multipart("/api/files/{noteId}/upload", noteId)
//                        .file(file)
//                        .param("noteId", String.valueOf(noteId)))
//                .andExpect(status().isOk()) // Expecting HTTP 200 OK
//                .andExpect(content().string(org.hamcrest.Matchers.containsString("http://localhost/api/files/serve/test.txt"))); // Expecting the file URL
//
//        // Verify that methods were called
//        verify(testNoteRepository, times(1)).findById(noteId);
//        verify(fileService, times(1)).uploadFile(eq(noteId), any(MultipartFile.class));
//    }

    @Test
    void uploadFileWithFalseNote() throws Exception {
        assertEquals(ResponseEntity.badRequest().body(null),
                fileController.uploadFiles(10L,
                        new MockMultipartFile("file", "testFile1.txt",
                                "text/plain", "test".getBytes())));
    }

    @Test
    void deleteFileSuccesTest(){
        List<File> files = fileService.getFilesByNoteId(-1L);
        assertEquals(ResponseEntity.ok("File deleted"), fileController.deleteFile(files.get(0).getId()));
        files = fileService.getFilesByNoteId(-1L);
        assertNotNull(files);
        assertEquals(1, files.size());
    }

    @Test
    void deleteFileFailureTest(){
        List<File> files = fileService.getFilesByNoteId(-1L);
        assertEquals(ResponseEntity.badRequest().body("Problem with deleting the file"), fileController.deleteFile(-1L));
        files = fileService.getFilesByNoteId(-1L);
        assertNotNull(files);
        assertEquals(2, files.size());
    }
}
