package deu.manager.executable.repository.interfaces;

import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.AdminStaff;

import java.util.List;
import java.util.Optional;

/**
 * staff_admin table에 접근하기 위한 repository interface입니다.
 */
public interface AdminStaffRepository {
    //Create
    AdminStaff save(AdminStaff staff) throws DbInsertWrongParamException;

    //Update
    void update(AdminStaff edit) throws DbInsertWrongParamException;

    //Read
    Optional<AdminStaff> findById(Long id);
    Optional<AdminStaff> findByStaffNum(int staffNum);
    List<AdminStaff> findByName(String name);

    //Delete
    void delete(List<Long> id);
    void delete(Long id);
}
