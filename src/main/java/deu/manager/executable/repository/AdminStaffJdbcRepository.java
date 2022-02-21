package deu.manager.executable.repository;


import deu.manager.executable.config.Tables;
import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.AdminStaff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdminStaffJdbcRepository implements AdminStaffRepository{
    private final JdbcTemplate jdbc;

    @Autowired
    public AdminStaffJdbcRepository(DataSource dataSource){
        jdbc = new JdbcTemplate(dataSource);
    }

    /**
     * staff_admin 데이터베이스에 데이터를 저장합니다.
     * key는 null, 그 외의 모든 값은 null이 아니어야 합니다.
     * @param input 저장하고자 하는 데이터
     * @return db에 저장된 데이터(자동생성된 key 포함)
     * @throws DbInsertWrongParamException 올바르지 않은 데이터가 input을 통해 들어올 경우 예외처리
     */
    @Override
    public AdminStaff save(AdminStaff input) throws DbInsertWrongParamException {
        // Table, GeneratedKey 설정
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName(Tables.AdminStaff.getValue())
                .usingGeneratedKeyColumns("id");

        // Null 검사
        if (input.getId() != null) {
            throw new DbInsertWrongParamException("Wrong param input: \"id\" is generated value", Tables.AdminStaff.getValue());
        }
        if (input.getName() == null ) {
            throw new DbInsertWrongParamException("Wrong param input : \"name\" can't be null", Tables.AdminStaff.getValue());
        }
        if (input.getStaffNum() == null ) {
            throw new DbInsertWrongParamException("Wrong param input : \"staffNum\" can't be null", Tables.AdminStaff.getValue());
        }
        if (input.getPassword() == null ) {
            throw new DbInsertWrongParamException("Wrong param input : \"password\" can't be null", Tables.AdminStaff.getValue());
        }
        if (input.getResidentNum() == null ) {
            throw new DbInsertWrongParamException("Wrong param input : \"residentNum\" can't be null", Tables.AdminStaff.getValue());
        }

        // https://hyeon9mak.github.io/easy-insert-with-simplejdbcinsert/
        // 각 parameter마다 이름을 지정, Map 객체로 생성 후 반환한다.
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", input.getName())
                .addValue("staff_num", input.getStaffNum())
                .addValue("password", input.getPassword())
                .addValue("resident_num", input.getResidentNum());
        Long key = insert.executeAndReturnKey(params).longValue();

        // 저장된 데이터의 key값을 포함해서 데이터를 리턴.
        return new AdminStaff(key, input.getName(), input.getStaffNum(), input.getPassword(), input.getResidentNum());
    }

    /**
     * id를 통해 데이터베이스 값을 수정하는 메소드. input의 id는 반드시 null이 아니어야 합니다.
     * @param input domain으로 표현된 수정하고자 하는 값
     * @throws DbInsertWrongParamException id가 null일 경우 예외처리
     */
    @Override
    public void update(AdminStaff input) throws DbInsertWrongParamException {
        // ID null check
        if(input.getId() == null ) {
            throw new DbInsertWrongParamException("Wrong param input: \"id\" cant be null when database update", Tables.AdminStaff.getValue());
        }

        // sql => UPDATE ${table_name} SET
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ")
                .append(Tables.AdminStaff.getValue()).append(" SET ");

        Map<String, Object> params = new HashMap<>();
        if(input.getName() != null) { params.put("name", input.getName()); }
        if(input.getPassword() != null) { params.put("password", input.getPassword()); }
        if(input.getResidentNum() != null) { params.put("resident_num", input.getResidentNum()); }

        boolean first = true;
        for (String key : params.keySet()) {
            if(!first) { sqlBuilder.append(", "); }
            first = false;
            sqlBuilder.append(key).append(" = '").append(params.get(key)).append("'");
        }
        sqlBuilder.append("WHERE id = ").append(input.getId());

        jdbc.update(sqlBuilder.toString());
    }

    /**
     * id를 통해 데이터베이스를 검색하는 메소드.
     * @param id 검색할 id
     * @return 검색 결과. Optional로 반환되기 때문에, isPresent(), get()을 사용해야 함
     * @see java.util.Optional
     */
    @Override
    public Optional<AdminStaff> findById(Long id) {
        List<AdminStaff> result = jdbc.query("select * from staff_admin where id = ?", adminStaffMapper(), id);
        return result.stream().findAny();
    }

    /**
     * 직원번호를 통해 데이터베이스를 검색하는 메소드.
     * @param staffNum 검색할 직원의 직원번호
     * @return 검색 결과. Optional로 반환되기 때문에, isPresent(), get()을 사용해야 함
     * @see java.util.Optional
     */
    @Override
    public Optional<AdminStaff> findByStaffNum(int staffNum) {
        List<AdminStaff> result = jdbc.query("select * from staff_admin where staff_num = ?", adminStaffMapper(), staffNum);
        return result.stream().findAny();
    }

    /**
     * 직원의 이름을 통해 데이터베이스를 검색하는 메소드.
     * @param name 검색할 직원의 이름
     * @return 같은 이름을 가진 직원들의 list
     */
    @Override
    public List<AdminStaff> findByName(String name) {
        return jdbc.query("select * from staff_admin where name = ?", adminStaffMapper(), name);
    }

    /**
     * id를 통해 직원 데이터를 삭제하는 메소드. 여러 id가 List로 들어올 시, 반복시행함
     * @param id 삭제할 직원의 id 리스트(직원번호는 입력 불가능)
     */
    @Override
    public void delete(List<Long> id) {
        for (Long i : id){
            this.delete(i);
        }
    }

    /**
     * id를 통해 직원 데이터를 삭제하는 메소드. 여러 id가 List로 들어올 시, 반복시행함
     * @param id 삭제할 직원의 id(직원번호는 입력 불가능)
     */
    @Override
    public void delete(Long id) {
        jdbc.update("delete from staff_admin where id = ?;", id);
    }

    private RowMapper<AdminStaff> adminStaffMapper() {
        return (rs, rowNum) -> AdminStaff.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .password(rs.getString("password"))
                .staffNum(rs.getInt("staff_num"))
                .residentNum(rs.getString("resident_num")).build();
    }
}
