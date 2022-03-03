package deu.manager.executable;

import deu.manager.executable.config.Tables;
import deu.manager.executable.config.exception.DbInsertWrongParamException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UniversityAdministrationManagerApplicationTests {
	Logger logger = LogManager.getLogger(this.getClass());

	@Test
	void contextLoads() {
	}

	@Test
	void loggerTest() {
		logger.info("info");
		try{
			throw new DbInsertWrongParamException("error!", Tables.Lecture.getValue());
		} catch (DbInsertWrongParamException e){
			logger.warn("exception occurred at: " + e.getTable(), e);
			logger.error("exception occurred at: " + e.getTable(), e);
			logger.fatal("exception occurred at: " + e.getTable(), e);

		}
	}

}
