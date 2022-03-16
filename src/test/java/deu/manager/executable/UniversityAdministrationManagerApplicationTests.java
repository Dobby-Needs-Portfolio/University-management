package deu.manager.executable;

import deu.manager.executable.config.ShutdownManager;
import deu.manager.executable.config.enums.Tables;
import deu.manager.executable.config.exception.database.DbInsertWrongParamException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UniversityAdministrationManagerApplicationTests {
	private final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	@Autowired private ShutdownManager shutdownManager;

	@Test
	void contextLoads() {
	}

	@RepeatedTest(2)
	void loggerTest() {
		log.info("this is info");
		try{
			throw new DbInsertWrongParamException("error!", Tables.Lecture.getValue());
		} catch (DbInsertWrongParamException e){
			log.warn("exception occurred at: " + e.getTable(), e);
			log.error("exception occurred at: " + e.getTable(), e);
			log.fatal("exception occurred at: " + e.getTable(), e);

		}
	}
}
