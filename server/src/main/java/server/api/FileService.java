package server.api;

import commons.File;
import commons.Note;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.database.FileRepository;
import server.database.NoteRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileService {


    private FileProperties fileProperties;
    private FileRepository fileRepository;
    private NoteRepository noteRepository;

    public FileService(FileProperties fileProperties, FileRepository fileRepository, NoteRepository noteRepository) {
        this.fileProperties= fileProperties;
        this.fileRepository = fileRepository;
        this.noteRepository = noteRepository;
    }

    public File uploadFile(Long noteId, MultipartFile file) {
        System.out.println("Uploading file: " + file.getOriginalFilename() + " for noteId: " + noteId);
        try{
            String uploadDir=fileProperties.getUploadDir();
            Note note = noteRepository.findById(noteId).orElseThrow(()-> new RuntimeException("The note was not found"));
            String fileName=file.getOriginalFilename();
            Path filePath= Paths.get(uploadDir,fileName);
            System.out.println("Saving file to: " + filePath.toString());
            Files.createDirectories(filePath.getParent());
            Files.write(filePath,file.getBytes());
            System.out.println("File saved successfully.");
            File fileSaved=new File(note,fileName,"/files/"+fileName,file.getContentType(),file.getSize());
            System.out.println("File saved to database with ID: " + fileSaved.getId());
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
            String uploadDir=fileProperties.getUploadDir();
            Path filePath=Paths.get(uploadDir,file.getFileName());
            Files.deleteIfExists(filePath);
            fileRepository.delete(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource loadAsResource(String fileName){
        System.out.println("Loading file as resource: " + fileName);
        try{
            String uploadDir=fileProperties.getUploadDir();
            Path filePath=Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource=new UrlResource(filePath.toUri());
            if(resource.exists()){
                if(resource.isReadable()){
                    return resource;
                }
                else{
                    throw new RuntimeException("File not readable"+fileName);
                }
            }
            else{
                throw new RuntimeException("File not found"+fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
