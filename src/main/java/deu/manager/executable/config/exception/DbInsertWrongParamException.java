package deu.manager.executable.config.exception;

import lombok.Getter;

@Getter
public class DbInsertWrongParamException extends Exception{
    public DbInsertWrongParamException(String Message, String table ) {
        super(Message);
        this.table = table;
    }
    public DbInsertWrongParamException(Throwable cause, String table ) {
        super(cause);
        this.table = table;
    }
    public DbInsertWrongParamException(String message, Throwable cause, String table) {
        super(message, cause);
        this.table = table;
    }

    String table;
}

