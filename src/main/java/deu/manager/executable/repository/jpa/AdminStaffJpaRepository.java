package deu.manager.executable.repository.jpa;

import deu.manager.executable.domain.AdminStaff;
import deu.manager.executable.repository.interfaces.AdminStaffRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminStaffJpaRepository extends JpaRepository<AdminStaff, Long>, AdminStaffRepository {

    @Query("select a from AdminStaff a where a.id = :id")
    Optional<AdminStaff> findById(@Param("id") Long id);
    @Override
    @Query("select s from AdminStaff s where s.staffNum = :staffNum")
    Optional<AdminStaff> findByStaffNum(@Param("staffNum") int staffNum);

    @Override
    @Query("select s from AdminStaff s where s.name = :name")
    List<AdminStaff> findByName(@Param("name") String name);

    @Override
    @Query("delete from AdminStaff s where s.id in (:ids)")
    void delete(@Param("ids") List<Long> id);

    @Override
    @Query("delete from AdminStaff s where s.id = :id")
    void delete(@Param("id") Long id);
}
