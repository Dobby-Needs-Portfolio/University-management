package deu.manager.executable.repository.interfaces;

import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    //Create
    Student save(Student student) throws DbInsertWrongParamException;

    //Update
    void update(Student param) throws DbInsertWrongParamException;

    //Read
    Optional<Student> findById(Long id);
    List<Student> findById(List<Long> id);
    Optional<Student> findByStudentNum(Integer studentId);
    List<Student> findByName(String name);

    //Delete
    void delete(List<Long> id) throws DbInsertWrongParamException;
    void delete(Long id) throws DbInsertWrongParamException;

    void deleteByMajor(List<Long> majorIds) throws DbInsertWrongParamException;
    void deleteByMajor(Long majorId) throws DbInsertWrongParamException;
}
