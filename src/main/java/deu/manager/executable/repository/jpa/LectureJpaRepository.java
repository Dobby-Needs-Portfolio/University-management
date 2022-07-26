package deu.manager.executable.repository.jpa;

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.repository.interfaces.LectureRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface LectureJpaRepository extends JpaRepository<Lecture, Long>, LectureRepository {

    @Override
    @Query("select l from Lecture l where l.id = :id")
    Optional<Lecture> searchById(@Param("id") Long id);

    @Override
    @Query("select l from Lecture l where l.id in (:ids)")
    List<Lecture> searchById(@Param("ids") List<Long> ids);

    @Override
    @Query("select l from Lecture l where l.lectureNum = :lectureNum")
    Optional<Lecture> searchByLectureNum(@Param("lectureNum") Integer lectureNum);

    @Override
    @Query("select l from Lecture l where l.name = :name")
    List<Lecture> searchByName(@Param("name") String name);

    @Override
    @Query("select l from Lecture l where l.professor.id = :profId")
    List<Lecture> searchByProfessor(@Param("profId") Long profId);

    @Override
    @Query("delete from Lecture l where l.id = :id")
    void delete(@Param("id") Long lectureId) throws DbInsertWrongParamException;

    @Override
    @Query("delete from Lecture l where l.id in (:ids)")
    void delete(@Param("ids") List<Long> ids) throws DbInsertWrongParamException;

    @Override
    @Query("delete from Lecture l where l.professor.id = :profId")
    void deleteByProfId(@Param("profId") Long profId) throws DbInsertWrongParamException;

    @Override
    @Query("delete from Lecture l where l.professor.id in (:profIds)")
    void deleteByProfId(@Param("profIds") List<Long> profIds) throws DbInsertWrongParamException;
}
