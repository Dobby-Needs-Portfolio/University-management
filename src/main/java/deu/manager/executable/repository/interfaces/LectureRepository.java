package deu.manager.executable.repository.interfaces;

import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Professor;

import java.util.List;
import java.util.Optional;

public interface LectureRepository {
    //Create
    Lecture save(Lecture lecture) throws DbInsertWrongParamException;

    //Read
    Optional<Lecture> searchById(Long id);
    List<Lecture> searchById(List<Long> ids);
    Optional<Lecture> searchByLectureNum(Integer lectureNum);
    List<Lecture> searchByName(String name);
    List<Lecture> searchByProfessor(Professor professor);

    //Update
    void update(Lecture update) throws DbInsertWrongParamException;

    //Delete
    void delete(Long id);
    void delete(List<Long> ids);
}
