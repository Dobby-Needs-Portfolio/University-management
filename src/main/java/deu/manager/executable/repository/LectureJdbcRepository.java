package deu.manager.executable.repository;

import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.repository.interfaces.LectureListenerRepository;
import deu.manager.executable.repository.interfaces.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class LectureJdbcRepository implements LectureRepository {
    JdbcTemplate jdbc;
    LectureListenerRepository llRepository;

    @Autowired
    public LectureJdbcRepository(
            DataSource dataSource,
            LectureListenerRepository llRepository){
        this.jdbc = new JdbcTemplate(dataSource);
        this.llRepository = llRepository;
    }

    @Override
    public Lecture save(Lecture lecture) throws DbInsertWrongParamException {
        return null;
    }

    @Override
    public Optional<Lecture> searchById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Lecture> searchById(List<Long> ids) {
        return null;
    }

    @Override
    public Optional<Lecture> searchByLectureNum(Integer lectureNum) {
        return Optional.empty();
    }

    @Override
    public List<Lecture> searchByName(String name) {
        return null;
    }

    @Override
    public List<Lecture> searchByProfessor(Long id) {
        return null;
    }

    @Override
    public void update(Lecture update) throws DbInsertWrongParamException {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void delete(List<Long> ids) {

    }
}
