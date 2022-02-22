package deu.manager.executable;

import deu.manager.executable.repository.MajorJdbcRepository;
import deu.manager.executable.repository.MajorRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.swing.*;
import javax.xml.crypto.Data;

/**
 * Test 환경에서 MajorRepository 를 Bean으로 설정하고 사용하기 위해서 만들어졌다.
 */
@Component
public class SpringConfig {

    private DataSource dataSource;

    public SpringConfig(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Bean
    public MajorRepository majorRepository(){
        return new MajorJdbcRepository(dataSource);
    }

}
