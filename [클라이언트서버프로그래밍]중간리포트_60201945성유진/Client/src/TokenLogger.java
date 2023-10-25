import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenLogger implements Closeable, Serializable {
    private static final long serialVersionUID = 1L;
    private transient FileWriter fileWriter;
    private String logFilePath;
    private SimpleDateFormat dateFormat;
    public TokenLogger(String logFilePath) {
        this.logFilePath = logFilePath;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");}
    public void log(String message) {
        System.out.println(message);
        String logMessageWithTimestamp = getLogMessageWithTimestamp(message);
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            writer.write(logMessageWithTimestamp);
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred", e);}}
    private String getLogMessageWithTimestamp(String message) {
        Date now = new Date();
        String formattedTimestamp = dateFormat.format(now);
        return message + " [" + formattedTimestamp + "]";}
    @Override
    public void close() throws IOException {
        if (fileWriter != null) {
            fileWriter.close();}}}
