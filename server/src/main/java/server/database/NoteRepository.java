package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import commons.Note;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Note save(Note entity);
}