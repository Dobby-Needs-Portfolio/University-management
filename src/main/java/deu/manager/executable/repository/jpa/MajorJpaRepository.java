package deu.manager.executable.repository.jpa;

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.Major;
import deu.manager.executable.repository.interfaces.MajorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MajorJpaRepository extends JpaRepository<Major, Long>, MajorRepository {

    // find by id
    @Query("select m from Major m where m.id = :id")
    Optional<Major> findById(@Param("id") Long id);

    @Override
    @Query("select m from Major m where m.name = :name")
    Optional<Major> findByName(@Param("name") String name);

    @Override
    @Query("delete from Major m where m.id = :id")
    void delete(@Param("id") Long id) throws DbInsertWrongParamException;
}
