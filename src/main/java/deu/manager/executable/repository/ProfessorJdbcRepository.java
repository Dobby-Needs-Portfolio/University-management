package deu.manager.executable.repository;

import deu.manager.executable.config.LazyFetcher;
import deu.manager.executable.config.Tables;
import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.repository.interfaces.LectureRepository;
import deu.manager.executable.repository.interfaces.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * professor 데이터베이스를 관리하는 repository class
 */
public class ProfessorJdbcRepository implements ProfessorRepository {

    private JdbcTemplate jdbc;
    private LectureRepository lectureRepository;
    //private LectureRepository lectureRepository;


    /**
     * ProfessorJdbcRepository 클래스의 생성자입니다. dependencies inject가 이루어져야 합니다.
     * @param dataSource DB 연결 정보를 담고 있는 dataSource
     * @param lectureRepository lecture 테이블을 관리하는 repository instance
     */
    @Autowired
    public ProfessorJdbcRepository(DataSource dataSource ,
                                   LectureRepository lectureRepository
                                   /*LectureRepository lectureRepository*/){
        this.jdbc = new JdbcTemplate(dataSource);
        this.lectureRepository = lectureRepository;
        //this.lectureRepository = lectureRepository;
    }




    /**
     * Professor 테이블에 데이터를 저장합니다. key는 null, 그 외의 모든 값은 null이 아니어야 합니다.
     * @param input 데이터베이스에 저장하고자 하는 데이터
     * @return db에 저장된 데이터(생성된 key 포함)
     * @throws DbInsertWrongParamException 올바르지 않은 데이터가 input을 통해 들어올 경우 thrown
     * @throws org.springframework.dao.DataAccessException unique 키의 중복 등의 sql query에서 일어난 오류 발생 시 thrown
     */
    @Override
    public Professor save(Professor input) throws DbInsertWrongParamException {

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName(Tables.Professor.getValue())
                .usingGeneratedKeyColumns("id");

        //Null 검사
        if (input.getId() != null) {
            throw new DbInsertWrongParamException("Wrong param input: \"id\" is generated value", Tables.Professor.getValue());
        }
        if (input.getName() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"name\" can't be null", Tables.Professor.getValue());
        }
        if (input.getProfessorNum() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"student_num\" can't be null", Tables.Professor.getValue());
        }
        if (input.getPassword() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"password\" can't be null", Tables.Professor.getValue());
        }
        if (input.getResidentNum() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"resident_num\" can't be null", Tables.Professor.getValue());
        }
        if (input.getMajor() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"Major\" can't be null", Tables.Professor.getValue());
        }
        if (input.getMajor().getId() == null || input.getMajor().getName() == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"Major element\" can't be null", Tables.Professor.getValue());
        }

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", input.getName())
                .addValue("prof_num", input.getProfessorNum())
                .addValue("password", input.getPassword())
                .addValue("resident_num", input.getResidentNum())
                .addValue("major", input.getMajor().getId());
        Long key = insert.executeAndReturnKey(params).longValue();

        return Professor.builder()
                .id(key)
                .name(input.getName())
                .professorNum(input.getProfessorNum())
                .password(input.getPassword())
                .residentNum(input.getResidentNum())
                .major(Major.builder()
                        .id(input.getMajor().getId())
                        .name(input.getMajor().getName())
                        .build()).build();

    }

    /**
     * professor 테이블의 값을 수정합니다.
     * @param input 수정할 값입니다. id는 수정하고자 하는 값을 가리키며, null이 될 수 없습니다.
     * @throws DbInsertWrongParamException input이 잘못 입력될 경우 발생합니다.
     * @throws org.springframework.dao.DataAccessException 외래키 에러 등 sql query에서의 에러 시 발생합니다.
     */
    @Override
    public void update(Professor input) throws DbInsertWrongParamException {

        // ID null check
        if (input.getId() == null) {
            throw new DbInsertWrongParamException("Wrong input input: \"id\" can't be null when database update", Tables.Professor.getValue());
        }
        if (input.getProfessorNum() != null){
            throw new DbInsertWrongParamException("Wrong input input: \"student_num\" should be null when database update", Tables.Professor.getValue());
        }

        // sql => UPDATE ${table_name} SET
        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(Tables.Professor.getValue()).append(" SET ");

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


        jdbc.update(sql.toString());


    }

    /**
     * table ID를 사용해서 professor 테이블을 검색합니다.
     * @param id 검색하고자 하는 교수의 id
     * @return 검색 결과. Optional 객체로 반환
     * @see Optional
     */
    @Override
    public Optional<Professor> findById(Long id) {
        List<Professor> result = jdbc.query(
                "SELECT p.id , p.name , p.prof_num , p.password , p.resident_num , m.id , m.name " +
                        "FROM professor p " + "INNER JOIN major m ON p.major = m.id " +
                        "WHERE p.id = ?", professorRowMapper(), id);
        return result.stream().findAny();
    }


    /**
     * table ID를 사용해서 professor 테이블을 검색합니다.
     * @param ids 검색하고자 하는 교수의 id 리스트
     * @return 검색 결과. List<Professor> 객체로 반환
     * @see Optional
     */
    @Override
    public List<Professor> findById(List<Long> ids) {
        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbc);
        String sql = "SELECT p.id , p.name , p.prof_num , p.password , p.resident_num , m.id , m.name " +
                        "FROM professor p " + "INNER JOIN major m ON p.major = m.id " +
                        "WHERE p.id in (:ids)";

        SqlParameterSource params = new MapSqlParameterSource().addValue("ids", ids);

        return namedJdbc.query(sql, params, professorRowMapper());
    }



    /**
     * professor 테이블을 professor_num을 사용해 검색합니다.
     * @param professorNum 검색하고자 하는 교수의 prof_num
     * @return 검색 결과. Optional 클래스에 wrapped 되어 반환.
     * @see Optional
     */
    @Override
    public Optional<Professor> findByProfessorNum(Integer professorNum) {
        List<Professor> result = jdbc.query("SELECT p.id , p.name , p.prof_num , p.password , p.resident_num , m.id , m.name " +
                "FROM professor p " + "INNER JOIN major m ON p.major = m.id " +
                "WHERE p.prof_num = ?", professorRowMapper(), professorNum);
        return result.stream().findAny();
    }



    /**
     * professor 테이블을 이름으로 검색합니다.
     * @param name 검색할 교수의 name
     * @return 검색 결과. 동명이인의 모든 교수가 List 형식으로 반환됩니다.
     */
    @Override
    public List<Professor> findByName(String name) {
        return jdbc.query("SELECT p.id , p.name , p.prof_num , p.password , p.resident_num , m.id , m.name " +
                "FROM professor p " + "INNER JOIN major m ON p.major = m.id " +
                "WHERE p.name = ?", professorRowMapper(), name);
    }


    /**
     * professor 테이블에서 데이터를 삭제합니다.
     * @param ids 삭제할 교수 데이터들의 id 리스트
     */
    @Override
    public void delete(List<Long> ids) {
        this.lectureRepository.delete(ids);

        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbc);
        SqlParameterSource params = new MapSqlParameterSource("ids", ids);

        // 1. ll에 lecture_id로 강의 삭제 인자 -> List<Long> lectureId -> lecture에서 인자 -> List<Long> prof_id
        // List<Lecture> findByProf_Id(List<Long> prof_id )
        //this.llRepository.deleteLecture(lectureRepository.findByProf_Id(ids));

        // 2. lecture에 교수가 담당한 강의 삭제 -> void deleteByProf_id(List<Long> prof_id)
        //this.lectureRepository.deleteByProf_Id(ids);

        // 3. prof 에서 교수 삭제
        nameJdbc.update("DELETE FROM professor WHERE id in (:ids)", params);

    }

    @Override
    public void delete(Long id) {
        //Delete dependency record first
        this.lectureRepository.delete(id);

        jdbc.update("DELETE FROM professor WHERE id = ?", id);

    }


    private RowMapper<Professor> professorRowMapper(){
        return ((rs, rowNum) -> {
            Long prof_id = rs.getLong("p.id");
            return Professor.builder()
                    .id(prof_id)
                    .name(rs.getString("p.name"))
                    .password(rs.getString("p.password"))
                    .professorNum(rs.getInt("p.prof_num"))
                    .residentNum(rs.getString("p.resident_num"))
                    .major(Major.builder()
                            .id(rs.getLong("m.id"))
                            .name(rs.getString("m.name")).build())
                    .lectures(new LazyFetcher<>(prof_id,this.lectureRepository::searchByProfessor))
                    .build();
        });
    }
}
