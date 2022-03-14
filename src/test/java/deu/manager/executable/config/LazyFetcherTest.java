package deu.manager.executable.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.*;
import java.util.*;


import static org.assertj.core.api.Assertions.*;

@Sql(value = "LazyFetcherTest_init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "LazyFetcherTest_delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class LazyFetcherTest {
    TestRepository testRepository = new TestRepository();

    @Test
    public void testFetch(){
        LazyFetcher<List<Long>, List<String>> lazyFetcher = testRepository.testRun();
        List<String> lazyFetched = lazyFetcher.get();
        assertThat(lazyFetched).isNotNull();
        assertThat(lazyFetched).containsExactly("Test1", "Test2", "Test4");
    }

}

class TestRepository{
    DataSource dataSource;
    JdbcTemplate jdbc;

    public TestRepository(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
//        dataSource.setUrl("jdbc:mariadb://localhost:3306/toyproject");
//        dataSource.setUsername("toyproject");
//        dataSource.setPassword("12345");

        Properties properties = new Properties();

        try{
            // Read .property - https://www.netjstech.com/2017/09/how-to-read-properties-file-in-java.html
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            dataSource.setDriverClassName(properties.getProperty("spring.datasource.driver-class-name"));
            dataSource.setUrl(properties.getProperty("spring.datasource.url"));
            dataSource.setUsername(properties.getProperty("spring.datasource.username"));
            dataSource.setPassword(properties.getProperty("spring.datasource.password"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.dataSource = dataSource;
        this.jdbc = new JdbcTemplate(dataSource);
    }

    LazyFetcher<List<Long>, List<String>> testRun(){
        return new LazyFetcher<>(new ArrayList<>(Arrays.asList(1L, 2L, 4L)), this::fetcher);
    }

    List<String> fetcher(List<Long> ids) {
        StringBuilder sql = new StringBuilder()
                .append("SELECT text FROM test_db.test_table WHERE id IN (");
        boolean first = true;
        for (Long id : ids) {
            if (!first) {
                sql.append(", ");
            }
            first = false;
            sql.append(id);
        }
        sql.append(");");
        System.out.println(sql);
        Optional<List<String>> query = this.jdbc.query(sql.toString(), testMapper()).stream().findAny();
        return query.orElse(null);
    }

    RowMapper<List<String>> testMapper(){
        return ((rs, rowNum) -> {
            List<String> dataList = new ArrayList<>();
            dataList.add(rs.getString("text"));
            while(rs.next()){
                dataList.add(rs.getString("text"));
            }
            return dataList;
        });
    }
}