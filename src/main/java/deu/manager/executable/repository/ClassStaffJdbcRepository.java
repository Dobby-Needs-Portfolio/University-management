package deu.manager.executable.repository;

import deu.manager.executable.config.Tables;
import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.ClassStaff;
import deu.manager.executable.repository.interfaces.ClassStaffRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ClassStaffJdbcRepository implements ClassStaffRepository {

    private JdbcTemplate jdbc;

    public ClassStaffJdbcRepository(DataSource dataSource){
        this.jdbc = new JdbcTemplate(dataSource);
    }


    /**
     * staff_class 데이터베이스에 데이터를 저장합니다.
     * key는 null, 그 외의 모든 값은 null이 아니어야 합니다.
     * @param input 저장하고자 하는 데이터
     * @return db에 저장된 데이터(자동생성된 key 포함)
     * @throws DbInsertWrongParamException 올바르지 않은 데이터가 input을 통해 들어올 경우 예외처리
     */
    @Override
    public ClassStaff save(ClassStaff input) throws DbInsertWrongParamException {

        // Table, GeneratedKey 설정
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName(Tables.ClassStaff.getValue())
                .usingGeneratedKeyColumns("id");

        // Null 검사
        if (input.getId() != null) {
            throw new DbInsertWrongParamException("Wrong param input: \"id\" is generated value", Tables.ClassStaff.getValue());
        }
        if (input.getName() == null ) {
            throw new DbInsertWrongParamException("Wrong param input : \"name\" can't be null", Tables.ClassStaff.getValue());
        }
        if (input.getStaffNum() == null ) {
            throw new DbInsertWrongParamException("Wrong param input : \"staffNum\" can't be null", Tables.ClassStaff.getValue());
        }
        if (input.getPassword() == null ) {
            throw new DbInsertWrongParamException("Wrong param input : \"password\" can't be null", Tables.ClassStaff.getValue());
        }
        if (input.getResidentNum() == null ) {
            throw new DbInsertWrongParamException("Wrong param input : \"residentNum\" can't be null", Tables.ClassStaff.getValue());
        }

        // https://hyeon9mak.github.io/easy-insert-with-simplejdbcinsert/
        // 각 parameter마다 이름을 지정, Map 객체로 생성 후 반환한다.
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", input.getName())
                .addValue("staff_num", input.getStaffNum())
                .addValue("password", input.getPassword())
                .addValue("resident_num", input.getResidentNum());

        // 저장된 객체 고유의 id(Key) 값을 반환하여 , 새로운 객체를 생성하여 반환할때 사용
        Long key = insert.executeAndReturnKey(params).longValue();

        return new ClassStaff(key, input.getName(), input.getStaffNum(), input.getPassword(), input.getResidentNum());
    }


    /**
     * id를 통해 데이터베이스 값을 수정하는 메소드. input의 id는 반드시 null이 아니어야 합니다.
     * @param input domain으로 표현된 수정하고자 하는 값
     * @throws DbInsertWrongParamException id가 null일 경우 예외처리
     */
    @Override
    public void update(ClassStaff input) throws DbInsertWrongParamException {

        // ID null check
        if(input.getId() == null ) {
            throw new DbInsertWrongParamException("Wrong param input: \"id\" cant be null when database update", Tables.ClassStaff.getValue());
        }

       StringBuilder sqlBuilder = new StringBuilder("update ")
               .append(Tables.ClassStaff.getValue()).append(" set ");

        HashMap<String, Object> params = new HashMap<>();
        if(input.getName() != null) { params.put("name", input.getName());}
        if(input.getName() != null) { params.put("password", input.getPassword());}
        if(input.getName() != null) { params.put("resident_num", input.getResidentNum());}

        boolean first = true;
        for(String key : params.keySet()){
            if(!first) {sqlBuilder.append(", ");}
            sqlBuilder.append(key).append(" = '").append(params.get(key)).append("' ");
            first = false;
        }

        sqlBuilder.append("where id = ").append(input.getId());
        jdbc.update(sqlBuilder.toString());

        //return new ClassStaff(input.getId(), input.getName(), input.getStaffNum(), input.getPassword(), input.getResidentNum());

    }








    /**
     * id를 통해 데이터베이스를 검색하는 메소드.
     * @param id 검색할 id
     * @return 검색 결과. Optional로 반환되기 때문에, isPresent(), get()을 사용해야 함
     * @see java.util.Optional
     */
    @Override
    public Optional<ClassStaff> findById(Long id) {
        List<ClassStaff> result = jdbc.query("select * from staff_class where id = ?", classStaffRowMapper(), id);

        return result.stream().findAny();
    }



    /**
     * 수업 담당자 번호를 통해 데이터베이스를 검색하는 메소드.
     * @param staffNum 검색할 직원의 직원번호
     * @return 검색 결과. Optional로 반환되기 때문에, isPresent(), get()을 사용해야 함
     * @see java.util.Optional
     */
    @Override
    public Optional<ClassStaff> findByStaffNum(int staffNum) {

        List<ClassStaff> result = jdbc.query("select * from staff_class where staff_num = ? ", classStaffRowMapper(), staffNum);
        return result.stream().findAny();

    }



    /**
     * 수업 담당자의 이름을 통해 데이터베이스를 검색하는 메소드.
     * @param name 검색할 직원의 이름
     * @return 같은 이름을 가진 직원들의 list
     */
    @Override
    public List<ClassStaff> findByName(String name) {
        List<ClassStaff> result = jdbc.query("select * from staff_class where name = ?", classStaffRowMapper(), name);
        return result;
    }



    /**
     * id를 통해 수업담당자의 데이터를 삭제하는 메소드. 여러 id가 List로 들어올 시, 반복시행함
     * @param id 삭제할 직원의 id 리스트(수업담당자 번호는 입력 불가능)
     */
    @Override
    public void delete(List<Long> id) {
        for(Long i :id){
            this.delete(i);
        }

    }


    /**
     * id를 통해 직원 데이터를 삭제하는 메소드. 여러 id가 List로 들어올 시, 반복시행함
     * @param id 삭제할 직원의 id(직원번호는 입력 불가능)
     */
    @Override
    public void delete(Long id) {
        jdbc.update("delete from staff_class where id = ?;", id);
    }

    private RowMapper<ClassStaff> classStaffRowMapper(){
        return new RowMapper<ClassStaff>() {
            @Override
            public ClassStaff mapRow(ResultSet rs, int rowNum) throws SQLException {
                ClassStaff classStaff = new ClassStaff(rs.getLong("id")
                        ,rs.getString("name")
                        ,rs.getInt("staff_num")
                        ,rs.getString("password")
                        ,rs.getString("resident_num"));
                return classStaff;
            }
        };
    }
}
