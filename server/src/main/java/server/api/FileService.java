package server.api;

import commons.File;
import commons.Note;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.database.FileRepository;
import server.database.NoteRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileService {
    
    private String uploadDir;

    private FileRepository fileRepository;
    private NoteRepository noteRepository;

    public FileService(FileRepository fileRepository, NoteRepository noteRepository) {
        this.fileRepository = fileRepository;
        this.noteRepository = noteRepository;
    }

    public File uploadFile(Long noteId, MultipartFile file) {
        try{
            Note note = noteRepository.findById(noteId).orElseThrow(()-> new RuntimeException("The note was not found"));
            String fileName=file.getName();
            Path filePath= Paths.get(uploadDir,fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath,file.getBytes());

            File fileSaved=new File(note,fileName,"/files/"+fileName,file.getContentType(),file.getSize());
            return fileRepository.save(fileSaved);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<File> getFilesByNoteId(Long noteId){
        return fileRepository.findByNoteId(noteId);
    }

    public void deleteFile(Long fileId){
        try{
            File file=fileRepository.findById(fileId).orElseThrow(()-> new RuntimeException("The file was not found"));
            Path filePath=Paths.get(uploadDir,file.getFileName());
            Files.deleteIfExists(filePath);
            fileRepository.delete(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
