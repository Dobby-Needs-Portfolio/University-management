package deu.manager.executable.repository.jpa;

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.ClassStaff;
import deu.manager.executable.repository.interfaces.ClassStaffRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassStaffJpaRepository extends JpaRepository<ClassStaff, Long>, ClassStaffRepository {

    @Query("select c from ClassStaff c where c.id = :id")
    Optional<ClassStaff> findById(@Param("id") Long id);

    @Override
    @Query("select c from ClassStaff c where c.staffNum = :staffNum")
    Optional<ClassStaff> findByStaffNum(@Param("staffNum") int staffNum);

    @Override
    @Query("select c from ClassStaff c where c.name = :name")
    List<ClassStaff> findByName(@Param("name") String name);

    @Override
    @Query("delete from ClassStaff c where c.id in (:ids)")
    void delete(@Param("ids") List<Long> ids) throws DbInsertWrongParamException;

    @Override
    @Query("delete from ClassStaff c where c.id = :id")
    void delete(@Param("id") Long id) throws DbInsertWrongParamException;
}
