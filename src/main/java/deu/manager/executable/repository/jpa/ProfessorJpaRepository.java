package deu.manager.executable.repository.jpa;

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.repository.interfaces.ProfessorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfessorJpaRepository extends JpaRepository<Professor, Long>, ProfessorRepository {

    @Override
    @Query("select p from Professor p where p.id = :id")
    List<Professor> findById(@Param("id") List<Long> id);

    @Override
    @Query("select p from Professor p where p.professorNum = :professorNum")
    Optional<Professor> findByProfessorNum(@Param("professorNum") Integer professorNum);

    @Override
    @Query("select p from Professor p where p.name = :name")
    List<Professor> findByName(@Param("name") String name);

    @Override
    @Query("delete from Professor p where p.id in (:ids)")
    void delete(@Param("ids") List<Long> ids) throws DbInsertWrongParamException;

    @Override
    @Query("delete from Professor p where p.id = :id")
    void delete(@Param("id") Long id) throws DbInsertWrongParamException;

    @Override
    @Query("delete from Professor p where p.major.id in (:ids)")
    void deleteByMajor(@Param("ids") List<Long> majorIds) throws DbInsertWrongParamException;

    @Override
    @Query("delete from Professor p where p.major.id = :id")
    void deleteByMajor(@Param("id") Long majorId) throws DbInsertWrongParamException;
}
