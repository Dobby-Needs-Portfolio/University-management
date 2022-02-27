package deu.manager.executable;

import deu.manager.executable.repository.AdminStaffJdbcRepository;
import deu.manager.executable.repository.interfaces.AdminStaffRepository;
import deu.manager.executable.repository.MajorJdbcRepository;
import deu.manager.executable.repository.interfaces.*;
import deu.manager.executable.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    private final DataSource dataSource;

    @Autowired
    public SpringConfig(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Bean
    public AdminStaffRepository adminStaffRepository(){
        return new AdminStaffJdbcRepository(dataSource);
    }

    @Bean
    public MajorRepository majorRepository(){
        return new MajorJdbcRepository(dataSource);
    }

    @Bean
    public ClassStaffRepository classStaffRepository(){
        return new ClassStaffJdbcRepository(dataSource);
    }
}
