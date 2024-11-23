package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import commons.Quote;

public interface NoteRepository extends JpaRepository<Quote, Long> {}