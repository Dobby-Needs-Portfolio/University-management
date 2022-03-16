package deu.manager.executable.repository.interfaces;

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.Professor;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository {
    //Create
    Professor save(Professor professor) throws DbInsertWrongParamException;

    //Update
    void update(Professor param) throws DbInsertWrongParamException;

    //Read
    Optional<Professor> findById(Long id);
    List<Professor> findById(List<Long> id);
    Optional<Professor> findByProfessorNum(Integer professorNum);

    List<Professor> findByName(String name);

    //Delete
    void delete(List<Long> id) throws DbInsertWrongParamException;
    void delete(Long id) throws DbInsertWrongParamException;

    void deleteByMajor(List<Long> majorIds) throws DbInsertWrongParamException;
    void deleteByMajor(Long majorId) throws DbInsertWrongParamException;
}
