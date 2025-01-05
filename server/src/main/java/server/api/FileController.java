package server.api;

import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import server.database.NoteRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private FileService fileService;
    private NoteRepository noteRepository;

    @Autowired
    public FileController(FileService fileService, NoteRepository noteRepository) {
        this.fileService = fileService;
        this.noteRepository = noteRepository;
    }

    @PostMapping("/{noteId}/upload")
    public ResponseEntity<List<String>>  listUploadedFiles(@PathVariable Long noteId){
        Optional<Note> noteOptional=noteRepository.findById(noteId);
        if(noteOptional.isEmpty()){
            return ResponseEntity.badRequest().body(null);
        }
        Note note=noteOptional.get();

        List<String> fileUrl=note.getFiles().stream()
                .map(fileName -> MvcUriComponentsBuilder.fromMethodName(FileController.class,
                        "serveFile", fileName).build().toUri().toString())
                .toList();
        return ResponseEntity.ok(fileUrl);
    }
}
