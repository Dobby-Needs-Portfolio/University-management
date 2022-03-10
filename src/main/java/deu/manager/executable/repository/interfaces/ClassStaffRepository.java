package deu.manager.executable.repository.interfaces;

import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.ClassStaff;

import java.util.List;
import java.util.Optional;

/**
 * staff_class table에 접근하기 위한 repository interface입니다.
 */
public interface ClassStaffRepository {

    //Create
    ClassStaff save(ClassStaff Staff) throws DbInsertWrongParamException;

    //Update
    void update(ClassStaff edit) throws DbInsertWrongParamException;

    //Read
    Optional<ClassStaff> findById(Long id);
    Optional<ClassStaff> findByStaffNum(int staffNum);
    List<ClassStaff> findByName(String name);

    //Delete
    void delete(List<Long> id) throws DbInsertWrongParamException;
    void delete(Long id) throws DbInsertWrongParamException;

}
