package deu.manager.executable.repository.interfaces;

import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.domain.Student;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository {
    //Create
    Professor save(Professor professor) throws DbInsertWrongParamException;

    //Update
    void update(Professor param) throws DbInsertWrongParamException;

    //Read
    Optional<Student> findById(Long id);
    List<Student> findById(List<Long> id);
    Optional<Student> findByProfessorNum(String name);
    List<Student> findByName(String name);

    //Delete
    void delete(List<Long> id);
    void delete(Long id);
}
