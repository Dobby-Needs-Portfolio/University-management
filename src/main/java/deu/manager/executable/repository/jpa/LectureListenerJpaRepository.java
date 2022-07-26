package deu.manager.executable.repository.jpa;

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.LectureListener;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.LectureListenerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LectureListenerJpaRepository extends JpaRepository<LectureListener, Long>, LectureListenerRepository {

    // find by id
    @Query("select l from LectureListener l where l.id = :id")
    Optional<LectureListener> findById(@Param("id") Long id);

    // todo : test 필요
    @Override
    @Query("select l.student from LectureListener l where l.lecture.id = :lectureId")
    List<Student> searchStudent(@Param("lectureId") Long lectureId);

    // todo : test 필요
    @Override
    @Query("select l.lecture from LectureListener l where l.student.id = :studentId")
    List<Lecture> searchLecture(@Param("studentId") Long studentId);

    @Override
    @Query("delete from LectureListener l where l.student.id = :studentId and l.lecture.id = :lectureId")
    void deleteSingle(@Param("studentId") Long studentId, @Param("lectureId") Long lectureId) throws DbInsertWrongParamException;

    @Override
    @Query("delete from LectureListener l where l.student.id = :studentId")
    void deleteStudent(@Param("studentId") Long studentId) throws DbInsertWrongParamException;

    @Override
    @Query("delete from LectureListener l where l.student.id in (:studentIds)")
    void deleteStudent(List<Long> studentIds) throws DbInsertWrongParamException;

    @Override
    @Query("delete from LectureListener l where l.lecture.id = :lectureId")
    void deleteLecture(Long lectureId) throws DbInsertWrongParamException;

    @Override
    @Query("delete from LectureListener l where l.lecture.id in (:lectureIds)")
    void deleteLecture(List<Long> lectureIds) throws DbInsertWrongParamException;
}
