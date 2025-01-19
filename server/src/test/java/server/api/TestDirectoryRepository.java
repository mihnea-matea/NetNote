package server.api;

import commons.Directory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.DirectoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestDirectoryRepository implements DirectoryRepository {
    public final List<Directory> directories = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String method) {
        calledMethods.add(method);
    }

    @Override
    public <S extends Directory> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<Directory> findAll() {
        return new ArrayList<>(directories);
    }

    @Override
    public List<Directory> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Directory entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Directory> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Optional<Directory> findById(Long id) {
        return directories.stream().filter(d -> d.getId() == id).findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        return directories.stream().anyMatch(d -> d.getId() == id);
    }

    @Override
    public Directory save(Directory directory) {
        directories.add(directory);
        return directory;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Directory> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Directory> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Directory> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Directory getOne(Long aLong) {
        return null;
    }

    @Override
    public Directory getById(Long aLong) {
        return null;
    }

    @Override
    public Directory getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Directory> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Directory> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Directory> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Directory> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Directory> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Directory> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Directory, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Directory> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Directory> findAll(Pageable pageable) {
        return null;
    }
}
