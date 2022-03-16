package deu.manager.executable.repository.interfaces;

import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.Major;
import java.util.Optional;

/**
 * major table에 접근하기 위한 repository interface입니다.
 */
public interface MajorRepository {

    //Create
    Major save(Major major) throws DbInsertWrongParamException;

    //Update
    void update(Major edit) throws DbInsertWrongParamException;

    //Read
    Optional<Major> findById(Long id);
    Optional<Major> findByName(String name);

    //Delete
    void delete(Long id) throws DbInsertWrongParamException;
}
