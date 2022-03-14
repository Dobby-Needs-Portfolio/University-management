package deu.manager.executable.config;

public enum ExitCode {
    SHUTDOWN_NORMALLY(0),
    JWT_PRIVATEKEY_FETCH_FAILURE(110);


    private final int value;
    ExitCode(int exitCode) { this.value = exitCode; }
    public int getValue() { return value; }
}
