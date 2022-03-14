package deu.manager.executable.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ShutdownManager {
    private final ApplicationContext appContext;
    private final Logger log = LogManager.getLogger(this.getClass());

    public ShutdownManager(ApplicationContext appContext){
        this.appContext = appContext;
    }

    /**
     * The methods to shutdown spring application, not JVM.
     * @param exitCode the code that Spring application will return when shutdown
     */
    public void shutdownInitiate(ExitCode exitCode){
        int returnCode = exitCode.getValue();
        log.warn("Server shutdown Initialized, return code - {}", returnCode);
        SpringApplication.exit(appContext, () -> returnCode);
    }
}
