package deu.manager.executable.repository.jdbc;

import deu.manager.executable.config.LazyFetcher;
import deu.manager.executable.config.enums.Tables;
import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.config.exception.database.DbUpdateRecordNotAvailable;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.repository.interfaces.LectureListenerRepository;
import deu.manager.executable.repository.interfaces.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.*;

/**
 * lecture 데이터베이스를 관리하는 repository class
 */
public class LectureJdbcRepository implements LectureRepository {
    JdbcTemplate jdbc;
    LectureListenerRepository llRepository;

    /**
     * LectureJdbcRepository 클래스의 생성자입니다. dependencies inject가 이루어져야 합니다.
     * @param dataSource DB 연결 정보를 담고 있는 dataSource
     * @param llRepository lecturelistener 테이블을 관리하는 repository instance
     */
    @Autowired
    public LectureJdbcRepository(
            DataSource dataSource,
            LectureListenerRepository llRepository){
        this.jdbc = new JdbcTemplate(dataSource);
        this.llRepository = llRepository;
    }

    /**
     * Lecture 테이블에 데이터를 저장합니다. key는 null, 그 외의 모든 값은 null이 아니어야 합니다.
     * @param input 데이터베이스에 저장하고자 하는 데이터
     * @return db에 저장된 데이터(생성된 key 포함)
     * @throws DbInsertWrongParamException 올바르지 않은 데이터가 input을 통해 들어올 경우 thrown
     * @throws org.springframework.dao.DataAccessException unique 키의 중복 등의 sql query에서 일어난 오류 발생 시 thrown
     */
    @Override
    public Lecture save(Lecture input) throws DbInsertWrongParamException {

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName(Tables.Lecture.getValue())
                .usingGeneratedKeyColumns("id");

        if(input.getId() != null){
            throw new DbInsertWrongParamException("Wrong param input: \"id\" is generated values", Tables.Lecture.getValue());
        }
        if(input.getLectureNum() == null){
            throw new DbInsertWrongParamException("Wrong param input: \"lecture_num\" can't be null", Tables.Lecture.getValue());
        }
        if(input.getName() == null){
            throw new DbInsertWrongParamException("Wrong param input: \"name\" can't be null", Tables.Lecture.getValue());
        }
        if(input.getProfessor() == null){
            throw new DbInsertWrongParamException("Wrong param input: \"professor\" can't be null", Tables.Lecture.getValue());
        }
        if(input.getMaxStudent() == null){
            throw new DbInsertWrongParamException("Wrong param input: \"max_student\" can't be null", Tables.Lecture.getValue());
        }
        if(input.getMinStudent() == null){
            throw new DbInsertWrongParamException("Wrong param input: \"min_student\" can't be null", Tables.Lecture.getValue());
        }
        //save할때는 무조건 false로 지정되어야 한다.
        if(input.getIsOpened() == true){
            throw new DbInsertWrongParamException("Wrong param input: \"is_opened\" can't be true now", Tables.Lecture.getValue());
        }
        if (input.getProfessor().getId() == null || input.getProfessor().getName() == null
            || input.getProfessor().getProfessorNum() == null || input.getProfessor().getPassword() == null
            || input.getProfessor().getResidentNum() == null || input.getProfessor().getMajor() == null)
        {
            throw new DbInsertWrongParamException("Wrong param input: \"Professor element\" can't be null", Tables.Lecture.getValue());
        }

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("lecture_num" , input.getLectureNum())
                .addValue("name",input.getName())
                .addValue("professor" , input.getProfessor().getId())
                .addValue("credit_unit",input.getCreditUnit())
                .addValue("max_student", input.getMaxStudent())
                .addValue("min_student",input.getMinStudent())
                .addValue("is_opened" , input.getIsOpened());

        Long key = insert.executeAndReturnKey(params).longValue();

        return Lecture.builder()
                .id(key)
                .lectureNum(input.getLectureNum())
                .name(input.getName())
                .professor(Professor.builder()
                        .id(input.getProfessor().getId())
                        .name(input.getProfessor().getName())
                        .professorNum(input.getProfessor().getProfessorNum())
                        .password(input.getProfessor().getPassword())
                        .residentNum(input.getProfessor().getResidentNum())
                        .major(Major.builder()
                                .id(input.getProfessor().getMajor().getId())
                                .name(input.getProfessor().getMajor().getName()).build()).build())
                .maxStudent(input.getMaxStudent())
                .minStudent(input.getMinStudent())
                .isOpened(input.getIsOpened())
                .creditUnit(input.getCreditUnit())
                .build();

    }

    /**
     * lecture 테이블의 값을 수정합니다.
     * @param input 수정할 값입니다. id는 수정하고자 하는 값을 가리키며, null이 될 수 없습니다.
     * @throws DbInsertWrongParamException input이 잘못 입력될 경우 발생합니다.
     * @throws org.springframework.dao.DataAccessException 외래키 에러 등 sql query에서의 에러 시 발생합니다.
     */
    @Override
    public void update(Lecture input) throws DbInsertWrongParamException, DbUpdateRecordNotAvailable {

        // ID null check
        if (input.getId() == null) {
            throw new DbInsertWrongParamException("Wrong input input: \"id\" can't be null when database update", Tables.Lecture.getValue());
        }
        if (input.getLectureNum() != null){
            throw new DbInsertWrongParamException("Wrong input input: \"lecture_num\" should be null when database update", Tables.Lecture.getValue());
        }


        /*
        *한번 개설된 강의에 대해서는 , 수정이 불가하므로 , update 쿼리가 실행 되기전에 input의 is_opened 필드 true인지 확인
        */
        String openCheckSql = "SELECT is_opened FROM lecture WHERE id = ?";
        Boolean isOpened = jdbc.query(openCheckSql,
                                    (rs, rowNum) -> { return rs.getBoolean("is_opened"); }, input.getId())
                                    .stream().findAny().get();
        if(isOpened){
            throw new DbUpdateRecordNotAvailable("Can't Update input: \\\"input\\\" can't be Update when Lecture is opened at once", Tables.Lecture.getValue());
        }


        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(Tables.Lecture.getValue()).append(" SET ");

        Map<String,Object> params = new HashMap<>();
        if(input.getName() != null){
            params.put("name" , input.getName());
        }
        if(input.getProfessor() != null){
            if(input.getProfessor().getId() != null)
            params.put("professor" , input.getProfessor().getId());
        }
        if(input.getCreditUnit() != null){
            params.put("credit_unit", input.getCreditUnit());
        }
        if(input.getMaxStudent() != null){
            params.put("max_student" , input.getMaxStudent());
        }
        if(input.getMinStudent() != null){
            params.put("min_student" , input.getMinStudent());
        }
        if(input.getIsOpened() != null){
            params.put("is_opened" , input.getIsOpened());
        }

        boolean first = true;
        for(String key : params.keySet()){
            if(!first) {sql.append(" , ");}
            if(key.equals("is_opened")){
                first = false;
                sql.append(key).append(" = ").append(params.get(key)).append(" ");
                continue;
            }
            first = false;
            sql.append(key).append(" = '").append(params.get(key)).append("' ");
        }
        sql.append(" WHERE id  = ").append(input.getId());

        jdbc.update(sql.toString());

    }


    /**
     * table ID를 사용해서 lecture 테이블을 검색합니다.
     * @param lectureId 검색하고자 하는 강의 id
     * @return 검색 결과. Optional 객체로 반환
     * @see Optional
     */
    @Override
    public Optional<Lecture> searchById(Long lectureId) {
        String sql = "SELECT l.id, l.lecture_num, l.name, l.credit_unit, l.max_student, l.min_student, l.is_opened, " +
                "p.id, p.name, p.prof_num, p.password, p.resident_num, m.id, m.name " +
                "FROM lecture l " +
                "INNER JOIN professor p on l.professor = p.id " +
                "INNER JOIN major m on p.major = m.id " +
                "WHERE l.id = ? " +
                "ORDER BY l.id";

        List<Lecture> result = jdbc.query(sql, lectureRowMapper(), lectureId);

        return result.stream().findAny();
    }

    /**
     * table ID를 사용해서 lecture 테이블을 검색합니다.
     * @param ids 검색하고자 하는 강의 id list
     * @return 검색 결과. List<lecture></lecture> 객체로 반환
     * @see Optional
     */
    @Override
    public List<Lecture> searchById(List<Long> ids) {

        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbc);

        String sql = "SELECT l.id, l.lecture_num, l.name, l.credit_unit, l.max_student, l.min_student, l.is_opened, " +
                "p.id, p.name, p.prof_num, p.password, p.resident_num, m.id, m.name " +
                "FROM lecture l " +
                "INNER JOIN professor p on l.professor = p.id " +
                "INNER JOIN major m on p.major = m.id " +
                "WHERE l.id in (:ids) " +
                "ORDER BY l.id";

        SqlParameterSource params = new MapSqlParameterSource().addValue("ids", ids);

        List<Lecture> result = namedJdbc.query(sql, params ,lectureRowMapper());
        return result;
    }

    /**
     * lecture 테이블을 lecture_num을 사용해 검색합니다.
     * @param lectureNum 검색하고자 하는 걍의 lecture_Num
     * @return 검색 결과. Optional 클래스에 wrapped 되어 반환.
     * @see Optional
     */
    @Override
    public Optional<Lecture> searchByLectureNum(Integer lectureNum) {
        String sql = "SELECT l.id, l.lecture_num, l.name, l.credit_unit, l.max_student, l.min_student, l.is_opened, " +
                "p.id, p.name, p.prof_num, p.password, p.resident_num, m.id, m.name " +
                "FROM lecture l " +
                "INNER JOIN professor p on l.professor = p.id " +
                "INNER JOIN major m on p.major = m.id " +
                "WHERE l.lecture_num = ? " +
                "ORDER BY l.id";

        List<Lecture> result = jdbc.query(sql, lectureRowMapper(), lectureNum);
        return result.stream().findAny();
    }


    /**
     * lecture 테이블을 name을 사용해 검색합니다.
     * @param lectureName 검색하고자 하는 걍의 name
     * @return 검색 결과. 같은 강의 이름을 가진 강의가 List 형식으로 반환
     * @see Optional
     */
    @Override
    public List<Lecture> searchByName(String lectureName) {
        String sql = "SELECT l.id, l.lecture_num, l.name, l.credit_unit, l.max_student, l.min_student, l.is_opened, " +
                "p.id, p.name, p.prof_num, p.password, p.resident_num, m.id, m.name " +
                "FROM lecture l " +
                "INNER JOIN professor p on l.professor = p.id " +
                "INNER JOIN major m on p.major = m.id " +
                "WHERE l.name = ? " +
                "ORDER BY l.id";

        List<Lecture> result = jdbc.query(sql, lectureRowMapper(), lectureName);
        return result;
    }

    /**
     * lecture 테이블을 prof_id을 사용해 검색합니다.
     * @param prof_id 검색하고자 하는 교수 id
     * @return 검색 결과. 교수가 담당하고 있는 강의 List 형식으로 반환
     * @see Optional
     */
    @Override
    public List<Lecture> searchByProfessor(Long prof_id) {
        String sql = "SELECT l.id, l.lecture_num, l.name, l.credit_unit, l.max_student, l.min_student, l.is_opened, " +
                "p.id, p.name, p.prof_num, p.password, p.resident_num, m.id, m.name " +
                "FROM lecture l " +
                "INNER JOIN professor p on l.professor = p.id " +
                "INNER JOIN major m on p.major = m.id " +
                "WHERE p.Id = ? " +
                "ORDER BY l.id";

        List<Lecture> result = jdbc.query(sql, lectureRowMapper(), prof_id);
        return result;
    }


    /**
     * lecture 테이블에서 데이터를 삭제합니다.
     * @param lectureId 삭제할 강의 데이터의 id
     */
    @Override
    public void delete(Long lectureId) throws DbInsertWrongParamException {
        if(lectureId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"lecture_id\" cant be null when database update",
                    Tables.Lecture.getValue());
        }

        llRepository.deleteLecture(lectureId);
        jdbc.update("DELETE FROM lecture WHERE id = ? ", lectureId);
    }

    /**
     * lecture 테이블에서 데이터를 삭제합니다.
     * @param lectureIds 삭제할 강의 데이터들의 ids
     */
    @Override
    public void delete(List<Long> lectureIds) throws DbInsertWrongParamException{
        if( lectureIds == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"lecture_id\" cant be null when database update",
                    Tables.Lecture.getValue());
        }

        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbc);
        SqlParameterSource params = new MapSqlParameterSource("ids", lectureIds);

        this.llRepository.deleteLecture(lectureIds);
        nameJdbc.update("DELETE FROM lecture WHERE id in (:ids) ", params);
    }

    @Override
    public void deleteByProfId(Long profId) throws DbInsertWrongParamException {
        if( profId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"prof_Id\" cant be null when database update",
                    Tables.Lecture.getValue());
        }

        //Delete ll first
        jdbc.update("DELETE ll FROM lecture_listener ll INNER JOIN lecture l on ll.lecture_id = l.id " +
                "INNER JOIN professor p on l.professor = p.id " +
                "WHERE p.id = ?", profId);

        //And delete l
        jdbc.update("DELETE l FROM lecture l " +
                "INNER JOIN professor p on l.professor = p.id " +
                "WHERE p.id = ?", profId);
    }

    @Override
    public void deleteByProfId(List<Long> profIds) throws DbInsertWrongParamException {
        if( profIds == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"prof_Id\" cant be null when database update",
                    Tables.Lecture.getValue());
        }

        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbc);
        //Solve method #1: use INNER JOIN on DELETE phrase
        //https://foxlime.tistory.com/160
        //Delete ll first
        SqlParameterSource profIdParam = new MapSqlParameterSource("ids", profIds);
        nameJdbc.update("DELETE ll FROM lecture_listener ll INNER JOIN lecture l on ll.lecture_id = l.id " +
                "INNER JOIN professor p on l.professor = p.id " +
                "WHERE p.id IN (:ids)", profIdParam);

        //And delete l
        nameJdbc.update("DELETE l FROM lecture l " +
                "INNER JOIN professor p on l.professor = p.id " +
                "WHERE p.id IN (:ids)", profIdParam);

//        //Solve method #2: SELECT once to get ID and delete
//        List<Long> lectureId = nameJdbc.query("SELECT l.id AS id FROM lecture l " +
//                "INNER JOIN professor p on l.professor = p.id " +
//                "WHERE p.id IN (:ids)", profIdParam, (rs, rowNum) -> rs.getLong("id"));
//
//        this.llRepository.deleteLecture(lectureId);
//        MapSqlParameterSource lectureIdsParam = new MapSqlParameterSource("ids", lectureId);
//        nameJdbc.update("DELETE FROM lecture WHERE id IN (:ids)", lectureIdsParam);
    }


    private RowMapper<Lecture> lectureRowMapper() {
        return ((rs, rowNum) -> {
            Long lectureId = rs.getLong("l.id");
            long professorId = rs.getLong("p.id");
            return Lecture.builder()
                    .id(lectureId)
                    .lectureNum(rs.getInt("l.lecture_num"))
                    .name(rs.getString("l.name"))
                    .creditUnit(rs.getInt("l.credit_unit"))
                    .maxStudent(rs.getInt("l.max_student"))
                    .minStudent(rs.getInt("l.min_student"))
                    .isOpened(rs.getBoolean("l.is_opened"))
                    .professor(Professor.builder()
                            .id(professorId)
                            .name(rs.getString("p.name"))
                            .professorNum(rs.getInt("p.prof_num"))
                            .password(rs.getString("p.password"))
                            .residentNum(rs.getString("p.resident_num"))
                            .lectures(new LazyFetcher<>(professorId,this::searchByProfessor))
                            .major(Major.builder()
                                    .id(rs.getLong("m.id"))
                                    .name(rs.getString("m.name"))
                                    .build()).build())
                    .studentList(new LazyFetcher<>(lectureId, this.llRepository::searchStudent)).build();
        });
    }

}
