import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ServerLogger implements Closeable, Serializable {
    private static final long serialVersionUID = 1L;
    private transient FileWriter fileWriter;
    public ServerLogger(String logFilePath) {
        File logFile = new File(logFilePath);
        try {
            this.fileWriter = new FileWriter(logFile, true);
        } catch (IOException e) {
            throw new RuntimeException("File is a directory or file could not be created or does not exist", e);}}
    public void log(String message) {
        System.out.println(message);
        try {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));
            String logMessage = timeStamp + " " + message;
            fileWriter.write(logMessage);
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) { throw new RuntimeException("I/O error occurred", e);}}
    public void logException(Exception e) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));
        String exceptionMessage = e.getMessage();
        String stackTrace = getStackTrace(e);
        String logMessage = timeStamp + " Exception: " + exceptionMessage + "\n" + stackTrace;
        log(logMessage);}
    @Override
    public void close() throws IOException { fileWriter.close();}
    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();}}
