package server.api;

import commons.File;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import server.database.NoteRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final NoteRepository noteRepository;
    private FileService fileService;

    @Autowired
    public FileController(FileService fileService, NoteRepository noteRepository) {
        this.fileService = fileService;
        this.noteRepository = noteRepository;
        System.out.println("controller initialized");
    }

    @GetMapping("/{noteId:\\d+}")
    public ResponseEntity<List<String>> listFiles(@PathVariable Long noteId){
        List<File> files=fileService.getFilesByNoteId(noteId);
        if (files.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        List<String> fileUrls=files.stream()
                .map(file -> MvcUriComponentsBuilder.fromMethodName(FileController.class,"serveFile", file.getFileName()).build().toUri().toString())
                .toList();
        return ResponseEntity.ok(fileUrls);
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename){
        try{
            Resource file=fileService.loadAsResource(filename);
            if(file==null){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; fileName=\"" +file.getFilename()+"\"").body(file);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/{noteId}/upload")
    public ResponseEntity<String> uploadFiles(@PathVariable Long noteId,@RequestParam("file") MultipartFile file){
        System.out.println("uploadFiles method triggered with noteId: " + noteId);
        Optional<Note> noteOptional=noteRepository.findById(noteId);
        if(noteOptional.isEmpty()){
            return ResponseEntity.badRequest().body(null);
        }
        Note note=noteOptional.get();
        System.out.println("Processing file: " + file.getOriginalFilename());
        File uploadedFile= fileService.uploadFile(noteId,file);
        String fileUrl=MvcUriComponentsBuilder.fromMethodName(FileController.class,"serveFile", uploadedFile.getFileName())
                .build()
                .toUri()
                .toString();
        return ResponseEntity.ok(fileUrl);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId){
        try{
            fileService.deleteFile(fileId);
            return ResponseEntity.ok("File deleted");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body("Problem with deleting the file");
        }
    }

//    @GetMapping("/download")
//    public ResponseEntity<Resource> downloadFile(@RequestParam String fileName){
//        try{
//
//        } catch(IOException e){
//
//        }
//    }
}
