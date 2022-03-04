package deu.manager.executable.repository;

import deu.manager.executable.config.Tables;
import deu.manager.executable.config.exception.DbInsertWrongParamException;

import deu.manager.executable.domain.Major;
import deu.manager.executable.repository.interfaces.MajorRepository;
import deu.manager.executable.repository.interfaces.ProfessorRepository;
import deu.manager.executable.repository.interfaces.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;


import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class MajorJdbcRepository implements MajorRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public MajorJdbcRepository(DataSource dataSource,
                               StudentRepository studentRepository,
                               ProfessorRepository professorRepository){
        this.jdbc = new JdbcTemplate(dataSource);
    }


    /**
     * major 데이터베이스에 데이터를 저장합니다.
     * key는 null, 그 외의 모든 값은 null이 아니어야 합니다.
     * @param input 저장하고자 하는 데이터
     * @return db에 저장된 데이터(자동생성된 key 포함)
     * @throws DbInsertWrongParamException 올바르지 않은 데이터가 input을 통해 들어올 경우 예외처리
     */
    @Override
    public Major save(Major input) throws DbInsertWrongParamException {

        // Table, GeneratedKey 설정
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName(Tables.Major.getValue())
                .usingGeneratedKeyColumns("id");

        // Null 검사
        if (input.getId() != null) {
            throw new DbInsertWrongParamException("Wrong param input: \"id\" is generated value", Tables.Major.getValue());
        }
        if (input.getName() == null ) {
            throw new DbInsertWrongParamException("Wrong param input : \"name\" can't be null", Tables.Major.getValue());
        }


        // https://hyeon9mak.github.io/easy-insert-with-simplejdbcinsert/
        // 각 parameter마다 이름을 지정, Map 객체로 생성 후 반환한다.
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", input.getName());

        // 저장된 데이터의 key값을 가지고 있다. key를 추후에 활용하기 위해 받았다.
        Long key = insert.executeAndReturnKey(params).longValue();

        // 저장된 데이터의 key값을 포함해서 데이터를 리턴.
        return new Major(key, input.getName());

    }

    /**
     * id를 통해 데이터베이스 값을 수정하는 메소드. input의 id는 반드시 null이 아니어야 합니다.
     * @param edit domain으로 표현된 수정하고자 하는 값
     * @throws DbInsertWrongParamException id가 null일 경우 예외처리
     */
    @Override
    public void update(Major edit) throws DbInsertWrongParamException {
        String updateSql = "update major set name = ? where id = ?";

        // ID null check
        if(edit.getId() == null ) {
            throw new DbInsertWrongParamException("Wrong param input: \"id\" cant be null when database update", Tables.Major.getValue());
        }
        // Name null check
        if(edit.getName() == null ) {
            throw new DbInsertWrongParamException("Wrong param input: \"id\" cant be null when database update", Tables.Major.getValue());
        }


        jdbc.update(updateSql, edit.getName(), edit.getId());

    }

    /**
     * id를 통해 데이터베이스를 검색하는 메소드.
     * @param id 검색할 id
     * @return 검색 결과. Optional로 반환되기 때문에, isPresent(), get()을 사용해야 함
     * @see java.util.Optional
     */
    @Override
    public Optional<Major> findById(Long id) {
        List<Major> result = jdbc.query("select * from major where id = ?", majorRowMapper(), id);
        return result.stream().findAny();
    }

    /**
     * 학과의 이름을 통해 데이터베이스를 검색하는 메소드.
     * @param name 검색할 학과의 이름
     * @return 검색한 학과를 List로 반환
     */
    @Override
    public Optional<Major> findByName(String name) {
        List<Major> result = jdbc.query("select * from major where name = ?", majorRowMapper(), name);
        return result.stream().findAny();
    }

    /**
     * id를 통해 학과 데이터를 삭제하는 메소드.
     * @param id 삭제할 학과의 id
     */
    @Override
    public void delete(Long id) {
        jdbc.update("delete from major where id = ?", id);

    }

    private RowMapper<Major> majorRowMapper() {
        return new RowMapper<Major>() {
            @Override
            public Major mapRow(ResultSet rs, int rowNum) throws SQLException {
                Major major = new Major(rs.getLong("id"),rs.getString("name"));
                return major;
            }
        };
    }


}
