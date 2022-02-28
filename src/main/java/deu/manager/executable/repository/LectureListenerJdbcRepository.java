package deu.manager.executable.repository;

import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.LectureListenerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.List;

public class LectureListenerJdbcRepository implements LectureListenerRepository {
    @Autowired
    public LectureListenerJdbcRepository(DataSource dataSource){

    }

    @Override
    public void save(Long studentId, Long lectureId) {

    }

    @Override
    public void save(Long studentId, List<Long> lectureId) {

    }

    @Override
    public List<Student> searchStudent(Long lectureId) {
        return null;
    }

    @Override
    public List<Lecture> searchLecture(Long studentId) {
        return null;
    }

    @Override
    public void deleteSingle(Long studentId, Long lectureId) {

    }

    @Override
    public void deleteStudent(Long studentId) {

    }

    @Override
    public void deleteStudent(List<Long> studentId) {

    }

    @Override
    public void deleteLecture(Long lectureId) {

    }

    @Override
    public void deleteLecture(List<Long> lectureId) {

    }
}
