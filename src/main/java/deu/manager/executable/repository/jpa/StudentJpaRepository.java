package deu.manager.executable.repository.jpa;

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.StudentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentJpaRepository extends JpaRepository<Student, Long>, StudentRepository {

    // find by id
    @Query("select s from Student s where s.id = :id")
    Optional<Student> findById(@Param("id") Long id);

    @Override
    @Query("select s from Student s where s.id = :ids")
    List<Student> findById(@Param("ids") List<Long> ids);

    @Override
    @Query("select s from Student s where s.studentNum = :studentNum")
    Optional<Student> findByStudentNum(@Param("studentNum") Integer studentId);

    @Override
    @Query("select s from Student s where s.name = :name")
    List<Student> findByName(@Param("name") String name);

    @Override
    @Query("delete from Student s where s.id in (:ids)")
    void delete(@Param("ids") List<Long> ids) throws DbInsertWrongParamException;

    @Override
    @Query("delete from Student s where s.id = :id")
    void delete(@Param("id") Long id) throws DbInsertWrongParamException;

    @Override
    @Query("delete from Student s where s.major.id in (:ids)")
    void deleteByMajor(@Param("ids") List<Long> majorIds) throws DbInsertWrongParamException;

    @Override
    @Query("delete from Student s where s.major.id = :id")
    void deleteByMajor(@Param("id") Long majorId) throws DbInsertWrongParamException;
}
