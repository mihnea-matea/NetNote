package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import commons.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
}