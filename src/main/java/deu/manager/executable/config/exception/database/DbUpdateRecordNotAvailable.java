package deu.manager.executable.config.exception.database;

public class DbUpdateRecordNotAvailable extends Exception{

    String table;

    public DbUpdateRecordNotAvailable(String Message , String table){
        super(Message);
        this.table = table;
    }

    public DbUpdateRecordNotAvailable(Throwable cause , String table){
        super(cause);
        this.table = table;
    }

    public DbUpdateRecordNotAvailable(String Message ,Throwable cause ,String table){
        super(Message , cause);
        this.table = table;
    }


}
