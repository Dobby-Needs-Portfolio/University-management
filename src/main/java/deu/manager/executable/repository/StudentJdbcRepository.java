package deu.manager.executable.repository;

import deu.manager.executable.config.LazyFetcher;
import deu.manager.executable.config.Tables;
import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.LectureListenerRepository;
import deu.manager.executable.repository.interfaces.MajorRepository;
import deu.manager.executable.repository.interfaces.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * student 데이터베이스를 관리하는 repository class
 */
public class StudentJdbcRepository implements StudentRepository {
    private JdbcTemplate jdbc;
    private LectureListenerRepository llRepository;

    /**
     * StudentJdbcRepository 클래스의 생성자입니다. dependencies inject가 이루어져야 합니다.
     * @param dataSource DB 연결 정보를 담고 있는 dataSource
     * @param llRepository lecture_listener 테이블을 관리하는 repository instance
     */
    @Autowired
    public StudentJdbcRepository(
            DataSource dataSource,
            LectureListenerRepository llRepository){
        this.jdbc = new JdbcTemplate(dataSource);
        this.llRepository = llRepository;
    }

    /**
     * student 테이블에 데이터를 저장합니다. key는 null, 그 외의 모든 값은 null이 아니어야 합니다.
     * @param input 데이터베이스에 저장하고자 하는 데이터
     * @return db에 저장된 데이터(생성된 key 포함)
     * @throws DbInsertWrongParamException 올바르지 않은 데이터가 input을 통해 들어올 경우 thrown
     * @throws org.springframework.dao.DataAccessException unique 키의 중복 등의 sql query에서 일어난 오류 발생 시 thrown
     */
    @Override
    public Student save(Student input) throws DbInsertWrongParamException {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName(Tables.Student.getValue())
                .usingGeneratedKeyColumns("id");

        //Null 검사
        if (input.getId() != null) {
            throw new DbInsertWrongParamException("Wrong param input: \"id\" is generated value", Tables.Lecture.getValue());
        }
        if (input.getName() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"name\" can't be null", Tables.Lecture.getValue());
        }
        if (input.getStudentNum() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"student_num\" can't be null", Tables.Lecture.getValue());
        }
        if (input.getPassword() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"password\" can't be null", Tables.Lecture.getValue());
        }
        if (input.getResidentNum() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"resident_num\" can't be null", Tables.Lecture.getValue());
        }
        if (input.getMajor() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"Major\" can't be null", Tables.Lecture.getValue());
        }
        if (input.getMajor().getId() == null || input.getMajor().getName() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"Major element\" can't be null", Tables.Lecture.getValue());
        }

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", input.getName())
                .addValue("student_num", input.getStudentNum())
                .addValue("password", input.getPassword())
                .addValue("resident_num", input.getResidentNum())
                .addValue("major", input.getMajor().getId());
        Long key = insert.executeAndReturnKey(params).longValue();

        return Student.builder()
                .id(key)
                .name(input.getName())
                .studentNum(input.getStudentNum())
                .password(input.getPassword())
                .residentNum(input.getResidentNum())
                .major(Major.builder()
                        .id(input.getMajor().getId())
                        .name(input.getMajor().getName())
                        .build()).build();
    }

    /**
     * student 테이블의 값을 수정합니다.
     * @param input 수정할 값입니다. id는 수정하고자 하는 값을 가리키며, null이 될 수 없습니다.
     * @throws DbInsertWrongParamException input이 잘못 입력될 경우 발생합니다.
     * @throws org.springframework.dao.DataAccessException 외래키 에러 등 sql query에서의 에러 시 발생합니다.
     */
    @Override
    public void update(Student input) throws DbInsertWrongParamException {
        // ID null check
        if (input.getId() == null) {
            throw new DbInsertWrongParamException("Wrong input input: \"id\" can't be null when database update", Tables.Student.getValue());
        }
        if (input.getStudentNum() != null){
            throw new DbInsertWrongParamException("Wrong input input: \"student_num\" should be null when database update", Tables.Student.getValue());
        }

        // sql => UPDATE ${table_name} SET
        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(Tables.Student.getValue()).append(" SET ");

        Map<String, Object> params = new HashMap<>();
        if (input.getName() != null) {
            params.put("name", input.getName());
        }
        if (input.getPassword() != null) {
            params.put("password", input.getPassword());
        }
        if (input.getResidentNum() != null) {
            params.put("resident_num", input.getResidentNum());
        }
        if (input.getMajor() != null) {
            if (input.getMajor().getId() != null){
                params.put("major", input.getMajor().getId());
            }
        }

        boolean first = true;
        for (String key: params.keySet()){
            if(!first) { sql.append(", "); }
            first = false;
            sql.append(key).append(" = '").append(params.get(key)).append("'");
        }
        sql.append(" WHERE ID = ").append(input.getId());

        //https://ifuwanna.tistory.com/221 - String, StringBuffer, StringBuilder 차이
        jdbc.update(sql.toString());
    }

    /**
     * table ID를 사용해서 student 테이블을 검색합니다.
     * @param id 검색하고자 하는 학생의 id
     * @return 검색 결과. Optional 객체로 반환
     * @see Optional
     */
    @Override
    public Optional<Student> findById(Long id) {
        List<Student> result = jdbc.query(
                "SELECT s.id, s.name, s.student_num, s.password, s.resident_num, m.id, m.name " +
                "FROM student s " +
                "INNER JOIN major m ON s.major = m.id " +
                "WHERE s.id = ?", studentRowMapper(), id);
        return result.stream().findAny();
    }

    /**
     * table ID를 사용해서 student 테이블을 검색합니다.
     * @param ids 검색하고자 하는 학생의 id 리스트
     * @return 검색 결과. Optional 객체로 반환
     * @see Optional
     */
    @Override
    public List<Student> findById(List<Long> ids) {
        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbc);
        String sql = "SELECT s.id, s.name, s.student_num, s.password, s.resident_num, m.id, m.name " +
                "FROM student s " +
                "INNER JOIN major m ON s.major = m.id " +
                "WHERE s.id IN (:ids)";
        SqlParameterSource param = new MapSqlParameterSource("ids", ids);
        return namedJdbc.query(sql, param, studentRowMapper());
    }

    /**
     * student 테이블을 student_num을 사용해 검색합니다.
     * @param studentNum 검색하고자 하는 학생의 student_num
     * @return 검색 결과. Optional 클래스에 wrapped 되어 반환.
     * @see Optional
     */
    @Override
    public Optional<Student> findByStudentNum(Integer studentNum) {
        List<Student> result = jdbc.query(
                "SELECT s.id, s.name, s.student_num, s.password, s.resident_num, m.id, m.name " +
                        "FROM student s " +
                        "INNER JOIN major m ON s.major = m.id " +
                        "WHERE s.student_num = ?", studentRowMapper(), studentNum);
        return result.stream().findAny();
    }

    /**
     * student 테이블을 이름으로 검색합니다.
     * @param name 검색할 학생의 name
     * @return 검색 결과. 동명이인의 모든 학생이 List 형식으로 반환됩니다.
     */
    @Override
    public List<Student> findByName(String name) {
        List<Student> result = jdbc.query(
                "SELECT s.id, s.name, s.student_num, s.password, s.resident_num, m.id, m.name " +
                        "FROM student s " +
                        "INNER JOIN major m ON s.major = m.id " +
                        "WHERE s.name = ?", studentRowMapper(), name);
        return result;
    }

    /**
     * student 테이블에서 데이터를 삭제합니다.
     * @param ids 삭제할 데이터들의 id 리스트
     */
    @Override
    public void delete(List<Long> ids) {
        //https://xlffm3.github.io/spring%20data/jdbctemplate-passing-list-as-in-clause/
        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbc);
        SqlParameterSource param = new MapSqlParameterSource("ids", ids);
        llRepository.deleteStudent(ids);
        namedJdbc.update("DELETE FROM student WHERE id IN (:ids)", param);
    }

    /**
     * student 테이블에서 데이터를 삭제합니다.
     * @param id 삭제할 데이터의 id
     */
    @Override
    public void delete(Long id) {
        llRepository.deleteStudent(id);
        jdbc.update("DELETE FROM student WHERE id = ?", id);
    }

    private RowMapper<Student> studentRowMapper() {
        return ((rs, rowNum) -> {
            Long student_id = rs.getLong("s.id");
            return Student.builder()
                    .id(student_id)
                    .name(rs.getString("s.name"))
                    .password(rs.getString("s.password"))
                    .studentNum(rs.getInt("s.student_num"))
                    .residentNum(rs.getString("s.resident_num"))
                    .major(Major.builder()
                            .id(rs.getLong("m.id"))
                            .name(rs.getString("m.name")).build())
                    .lectureList(new LazyFetcher<Long, List<Lecture>>(student_id, this.llRepository::searchLecture))
                    .build();
        });
    }
}
