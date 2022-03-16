package deu.manager.executable.repository;

import deu.manager.executable.config.LazyFetcher;
import deu.manager.executable.config.enums.Tables;
import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import deu.manager.executable.domain.Lecture;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.LectureListenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LectureListenerJdbcRepository implements LectureListenerRepository {
    private final JdbcTemplate jdbc;

    @Autowired
    public LectureListenerJdbcRepository(DataSource dataSource){
        this.jdbc = new JdbcTemplate(dataSource);
    }

    /**
     * lecture_listener 데이터베이스에 데이터를 저장합니다. 두 parameter 다 null일 수 없습니다.
     * @param studentId 대상 학생의 id
     * @param lectureId 대상 강의의 id
     * @throws DbInsertWrongParamException id가 null로 들어올 시 예외처리
     */
    //Create
    @Override
    public void save(Long studentId, Long lectureId) throws DbInsertWrongParamException {
        if(studentId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"student_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }
        if(lectureId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"lecture_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }
        jdbc.update("INSERT INTO lecture_listener (student_id, lecture_id) VALUE (?, ?)", studentId, lectureId);
    }

    //https://stackoverflow.com/questions/3165730/inserting-multiple-rows-using-jdbctemplate
    //Insert multiple rows in JDBCTemplate - BatchPreparedStatementSetter()

    //Need transaction - 데이터 입력 중 중간에 에러 발생 시 트랜잭션을 통한 rollback 필요
    //나중에 확인할거 - https://stackoverflow.com/questions/39096860/roll-back-a-if-b-goes-wrong-spring-boot-jdbctemplate
    /**
     * 일반적인 save 메소드와 같은 기능을 하나, 리스트를 통해 저장 가능합니다. 두 parameter 다 null일 수 없습니다.<br>
     * 사용에는 반드시 transaction을 병행하여 사용하시기 바랍니다.
     * @param studentId - 대상 학생의 id
     * @param lectureId - 대상 강의의 id list
     * @throws DbInsertWrongParamException id가 null로 들어올 시 예외처리
     * @see List
     */
    @Override
    public void save(Long studentId, List<Long> lectureId) throws DbInsertWrongParamException{
        if(studentId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"student_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }
        if(lectureId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"lecture_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }
        if(lectureId.isEmpty()) return;
        String sql = "INSERT INTO lecture_listener (student_id, lecture_id) VALUES (?, ?)";

        jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, studentId);
                ps.setLong(2, lectureId.get(i));
            }

            @Override
            public int getBatchSize() {
                return lectureId.size();
            }
        });
    }

    //Read

    /**
     * 강의의 ID를 통해 강의를 수강하는 학생 리스트를 들고옵니다.
     * @param lectureId 학생 리스트를 불러오고자 하는 강의의 id
     * @return 강의를 수강하는 학생 리스트
     */
    @Override
    public List<Student> searchStudent(Long lectureId) {
        String sql = "SELECT s.id, s.name, s.student_num, s.password, s.resident_num, m.id, m.name " +
                "FROM lecture_listener ll " +
                "INNER JOIN student s on ll.student_id = s.id " +
                "INNER JOIN major m on s.major = m.id " +
                "WHERE ll.lecture_id = ? " +
                "ORDER BY s.id";
        return jdbc.query(sql, studentRowMapper(), lectureId);
    }

    /**
     * 학생의 ID를 통해 학생이 수강하는 강의 리스트를 불러옵니다.
     * @param studentId 강의 리스트를 불러오고자 하는 학생의 id
     * @return 학생이 수강하는 강의 리스트
     */
    @Override
    public List<Lecture> searchLecture(Long studentId) {
        String sql = "SELECT l.id, l.lecture_num, l.name, l.credit_unit, l.max_student, l.min_student, l.is_opened, " +
                "p.id, p.name, p.prof_num, p.password, p.resident_num, m.id, m.name " +
                "FROM lecture_listener ll " +
                "INNER JOIN lecture l on ll.lecture_id = l.id " +
                "INNER JOIN professor p on l.professor = p.id " +
                "INNER JOIN major m on p.major = m.id " +
                "WHERE ll.student_id = ? " +
                "ORDER BY l.id";
        return jdbc.query(sql, lectureRowMapper(), studentId);
    }

    /* TODO: getBills 메소드 생성/구현.
    *   학생 ID, 강의 ID를 받거나, 학생 ID를 입력받으면 수강료 리스트 또는 객체를 리턴하는 함수 필요*/

    //Update
    /**
     * 학생 id, 강의 id로 이루어진 레코드에 수강료 데이터를 추가합니다.<br>
     * parameter로 입력되는 값들은 null일 수 없습니다.
     * @param studentId 수정하고자 하는 대상 레코드의 학생 id
     * @param lectureId 수정하고자 하는 대상 레코드의 강의 id
     * @param price 추가할 수강료 금액
     * @throws DbInsertWrongParamException 값이 잘못될 시 예외처리
     */
    @Override
    public void updatePrice(List<Long> studentId, Long lectureId, Integer price) throws DbInsertWrongParamException{
        if(studentId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"student_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }
        if(lectureId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"lecture_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }
        if(price == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"price\" cant be null when database update", Tables.LectureListener.getValue());
        }

        String sql = "UPDATE lecture_listener SET bills_price = ? " +
                "WHERE student_id = ? AND lecture_id = ?";
        jdbc.update(sql, price, studentId, lectureId);
    }

    /**
     * 일반적인 lecturelistener와 기능은 동일하나, lectureId에 해당하는 레코드의 값을 <b>전부</b> 수정합니다.
     * @param lectureId 수정하고자 하는 대상 레코드들의 강의 id
     * @param price 추가할 수강료 금액
     * @throws DbInsertWrongParamException - 값이 잘못될 시 예외처리
     */
    @Override
    public void updatePrice(Long lectureId, Integer price) throws DbInsertWrongParamException{
        if(lectureId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"lecture_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }
        if(price == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"price\" cant be null when database update", Tables.LectureListener.getValue());
        }

        String sql = "UPDATE lecture_listener SET bills_price = ? " +
                "WHERE lecture_id = ?";
        jdbc.update(sql, price, lectureId);
    }

    //Delete
    /**
     * studentId, lectureID로 이루어진 레코드를 삭제하는 메소드
     * @param studentId 삭제하고자 하는 대상 레코드의 학생 id
     * @param lectureId 삭제하고자 하는 대상 레코드의 강의 id
     * @throws DbInsertWrongParamException 값이 잘못될 시 예외처리
     */
    @Override
    public void deleteSingle(Long studentId, Long lectureId) throws DbInsertWrongParamException {
        if(studentId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"student_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }
        if(lectureId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"lecture_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }

        jdbc.update("DELETE FROM lecture_listener WHERE student_id = ? AND lecture_id = ?", studentId, lectureId);
    }

    /**
     * 입력된 studentId에 해당되는 모든 레코드를 삭제하는 메소드
     * @param studentId 삭제하고자 하는 대상 레코드들의 학생 id
     * @throws DbInsertWrongParamException studentID가 null일 시 예외 발생
     */
    @Override
    public void deleteStudent(Long studentId) throws DbInsertWrongParamException {
        if(studentId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"student_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }

        jdbc.update("DELETE FROM lecture_listener WHERE student_id = ?", studentId);
    }

    /**
     * 입력된 studentId 리스트에 해당되는 모든 레코드를 삭제하는 메소드
     * @param studentId 삭제하고자 하는 대상 레코드들의 학생 id 리스트
     * @throws DbInsertWrongParamException studentId가 null일 시 발생
     */
    @Override
    public void deleteStudent(List<Long> studentId) throws DbInsertWrongParamException {
        if(studentId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"student_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }

        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbc);
        SqlParameterSource param = new MapSqlParameterSource("student_ids", studentId);
        namedJdbc.update("DELETE FROM lecture_listener WHERE student_id IN (:student_ids)", param);
    }

    /**
     * 입력된 lectureId에 해당하는 모든 레코드를 삭제하는 메소드
     * @param lectureId 삭제하고자 하는 대상 레코드들의 강의 id
     * @throws DbInsertWrongParamException lectureID가 null일 시 발생
     */
    @Override
    public void deleteLecture(Long lectureId) throws DbInsertWrongParamException {
        if(lectureId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"lecture_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }

        jdbc.update("DELETE FROM lecture_listener WHERE lecture_id = ?", lectureId);
    }

    /**
     * 입력된 lectureId 리스트에 해당하는 모든 레코드를 삭제하는 메소드
     * @param lectureId 삭제하고자 하는 대상 레코드들의 강의 id 리스트
     * @throws DbInsertWrongParamException lectureId가 null일 시 발생
     */
    @Override
    public void deleteLecture(List<Long> lectureId) throws DbInsertWrongParamException {
        if(lectureId == null) {
            throw new DbInsertWrongParamException("Wrong param input: \"lecture_id\" cant be null when database update",
                    Tables.LectureListener.getValue());
        }

        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbc);
        SqlParameterSource param = new MapSqlParameterSource("lecture_ids", lectureId);
        namedJdbc.update("DELETE FROM lecture_listener WHERE lecture_id IN (:lecture_ids)", param);
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
                    .lectureList(new LazyFetcher<>(student_id, this::searchLecture))
                    .build();
        });
    }

    // 핵심 요소
    // Search depth는 2로 제한. 여기서는 학생 -> 강의 -> 교수까지만 가능하고, 교수의 강의 리스트는 받아올 수 없음.
    // 해당 서비스는 서비스 모델에서 구현해야 하며, 추가적인 쿼리를 발생시켜야 한다.
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
                            .lectures(null)
                            .major(Major.builder()
                                    .id(rs.getLong("m.id"))
                                    .name(rs.getString("m.name"))
                                    .build()).build())
                    .studentList(new LazyFetcher<>(lectureId, this::searchStudent)).build();
        });
    }
}
