import java.io.Serializable;
import java.util.StringTokenizer;

public class Register implements Serializable{
	private static final long serialVersionUID = 1L;
	protected String studentId;
	protected String courseId;
	protected String registerDate;
	public Register(String inputString) {
        StringTokenizer stringTokenizer = new StringTokenizer(inputString);
    	this.studentId = stringTokenizer.nextToken();
    	this.courseId = stringTokenizer.nextToken();
    	this.registerDate = stringTokenizer.nextToken();}	
    public String toString() {
        String stringReturn = this.studentId + " " + this.courseId + " " + registerDate;
        return stringReturn;}}
