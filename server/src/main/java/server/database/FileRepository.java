package server.database;

import commons.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Long> {
    List<File> findByNoteId(Long noteId);
}
