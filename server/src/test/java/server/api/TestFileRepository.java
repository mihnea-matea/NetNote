package server.api;

import commons.File;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.FileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestFileRepository implements FileRepository {
    public final List<File> files = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String method) {
        calledMethods.add(method);
    }

    @Override
    public <S extends File> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<File> findAll() {
        calledMethods.add("findAll");
        return new ArrayList<>(files);
    }

    @Override
    public List<File> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long id) {
        for(File file : files) {
            if(file.getId() == id){
                files.remove(file);
                break;
            }
        }
    }

    @Override
    public void delete(File entity) {
        files.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends File> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Optional<File> findById(Long id) {
        for(File file : files) {
            if(file.getId() == id){
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    @Override
    public File findByFileName(String fileName){
        for(File file : files){
            if(file.getFileName().equals(fileName)){
                return file;
            }
        }
        return null;
    }

    @Override
    public List<File> findByFileType(String fileType){
        List<File> files = new ArrayList<>();
        for(File file : files){
            if(file.getFileType().equals(fileType)){
                files.add(file);
            }
        }
        return files;
    }

    @Override
    public List<File> findByNoteId(Long noteId) {
        return files.stream()
                .filter(file -> file.getNote().getId() == noteId)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByNoteId(Long noteId){

    }

    @Override
    public boolean existsById(Long id) {
        for(File file : files) {
            if(file.getId() == id){
                return true;
            }
        }
        return false;
    }

    @Override
    public File save(File file) {
        files.add(file);
        return file;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends File> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends File> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<File> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public File getOne(Long Long) {
        return null;
    }

    @Override
    public File getById(Long id) {
        for(File file : files) {
            if(file.getId() == id){
                return file;
            }
        }
        return null;
    }

    @Override
    public File getReferenceById(Long Long) {
        return null;
    }

    @Override
    public <S extends File> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends File> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends File> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends File> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends File> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends File> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends File, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<File> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<File> findAll(Pageable pageable) {
        return null;
    }
}
