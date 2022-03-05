package deu.manager.executable.repository.interfaces;

import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Student;

import java.util.List;

public interface LectureListenerRepository {
    //Create
    void save(Long studentId, Long lectureId) throws DbInsertWrongParamException;
    void save(Long studentId, List<Long> lectureId) throws DbInsertWrongParamException;

    //Read
    List<Student> searchStudent(Long lectureId);
    List<Lecture> searchLecture(Long studentId);


    //Update
    void updatePrice(List<Long> studentId, Long lectureId, Integer price) throws DbInsertWrongParamException;
    void updatePrice(Long lectureId, Integer price) throws DbInsertWrongParamException;

    //Delete
    void deleteSingle(Long studentId, Long lectureId) throws DbInsertWrongParamException;
    void deleteStudent(Long studentId) throws DbInsertWrongParamException;
    void deleteStudent(List<Long> studentId) throws DbInsertWrongParamException;
    void deleteLecture(Long lectureId) throws DbInsertWrongParamException;
    void deleteLecture(List<Long> lectureId) throws DbInsertWrongParamException;
}
