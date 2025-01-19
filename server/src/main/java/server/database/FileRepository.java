package server.database;

import commons.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FileRepository extends JpaRepository<File,Long> {
    List<File> findByNoteId(Long noteId);
    File findByFileName(String fileName);
    void deleteByNoteId(Long noteId);
    List<File> findByFileType(String fileType);
}
