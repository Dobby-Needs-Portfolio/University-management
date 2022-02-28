package deu.manager.executable.repository.interfaces;

import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Student;

import java.util.List;

public interface LectureListenerRepository {
    //Create
    void save(Long studentId, Long lectureId);
    void save(Long studentId, List<Long> lectureId);

    //Read
    List<Student> searchStudent(Long lectureId);
    List<Lecture> searchLecture(Long studentId);

    //Delete
    void deleteSingle(Long studentId, Long lectureId);
    void deleteStudent(Long studentId);
    void deleteStudent(List<Long> studentId);
    void deleteLecture(Long lectureId);
    void deleteLecture(List<Long> lectureId);
}
