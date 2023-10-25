import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Server extends UnicastRemoteObject implements ServerIF {
	private static final long serialVersionUID = 1L;
	private static ServerLogger logger = new ServerLogger("ServerLog.log");
	private DAO dao;
    protected Server() throws RemoteException {
        super();
        String dbURL = "jdbc:mysql://localhost:3306/register_db-schema";
        String dbUser = "root";
        String dbPassword = "Gmlakddfl9529";
        try {
            dao = new DAO(dbURL, dbUser, dbPassword);
        } catch (ClassNotFoundException | SQLException e) {
            logger.logException(e);
            e.printStackTrace();}}
    public static void main(String[] arg) throws NotBoundException {
        try {
            Server server = new Server();
            Naming.rebind("Server", server);
            System.out.println("Server is ready !!!");
        } catch (RemoteException | MalformedURLException e) {
            logger.logException(e);
            e.printStackTrace();}}
	@Override
	public ArrayList<Student> getAllStudentData() throws RemoteException, NullDataException {
		return dao.getAllStudentData();}
	@Override
	public ArrayList<Course> getAllCourseData() throws RemoteException, NullDataException {
		return dao.getAllCourseData();}
	@Override
	public ArrayList<Register> getAllRegisterData() throws RemoteException, NullDataException {
		return dao.getAllRegisterData();}
	@Override
	public boolean addStudent(String studentInfo) throws RemoteException, NullDataException, DuplicateDataException {
		return dao.addCourse(studentInfo);}
	@Override
	public boolean deleteStudent(String studentId) throws RemoteException {
		return dao.deleteStudent(studentId);}
	@Override
	public boolean addCourse(String courseInfo) throws RemoteException, NullDataException, DuplicateDataException {
		return dao.addCourse(courseInfo);}
	@Override
	public boolean deleteCourse(String courseId) throws RemoteException {
		return dao.deleteCourse(courseId);}
	@Override
	public boolean registerCourse(String registerInfo) throws RemoteException, NullDataException, DuplicateDataException {
	    if ( checkPreCourses(registerInfo)) {
	        return dao.registerCourse(registerInfo);
	    } else {System.out.println("Course registration failed due to missing prerequisites."); return false;}}
	@Override
	public boolean cancelRegistration(String studentId) throws RemoteException {
		return dao.cancelRegistration(studentId);}
	@Override
	public boolean loginUser(String studentId, String password, String token) throws RemoteException, NullDataException,
	        TokenDifferentException, TokenTimeOutException {
	    boolean loginSuccess = dao.readUser(studentId, password, token);
		if (loginSuccess) { String logMessage = "User " + studentId + " logged in successfully.";
		    logger.log(logMessage);
		} else { String logMessage = "Login failed for user " + studentId;
		    logger.log(logMessage);} return loginSuccess;}
	@Override
	public Token signForUser(String studentId, String newPassword)
	        throws RemoteException, NullDataException, DuplicateDataException {
	    Student existingStudent = getStudent(studentId);
	    if (existingStudent == null) { return null;}
	    Token token = new Token(1, generateToken(), System.currentTimeMillis());
	    boolean signSuccess = dao.addUser(studentId, newPassword, token.getToken());
	    if (signSuccess) {
	        String logMessage = "User " + studentId + " signed up successfully ";
	        logger.log(logMessage);
	        dao.addUser(studentId, newPassword, token.getToken()); return token;
	    } else { logger.log("User signup failed for " + studentId); return null;}}
	public Token getToken(String studentId) throws RemoteException {
		Token token = new Token(1, generateToken(), System.currentTimeMillis());
		String logMessage = "Token issued for student: " + studentId + " with token: " + token.getToken();
		token.logTokenInfo(logMessage); return token;}
	private String generateToken() {
		int length = 32;
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder token = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(characters.length());
			token.append(characters.charAt(index));}
		return token.toString();}
	public Student getStudent(String studentId) throws RemoteException, NullDataException {
		for (Student student : getAllStudentData()) {
			if (student.match(studentId)) { return student;
			}} return null;}
	public Course getCourse(String courseId) throws RemoteException, NullDataException {
		for (Course course : getAllCourseData()) {
			if (course.match(courseId)) { return course;
			}} return null;}
	public boolean checkPreCourses(String registerInfo) throws RemoteException, NullDataException {
		Register register = new Register(registerInfo);
		Student student = getStudent(register.studentId);
		Course course = getCourse(register.courseId);
		if (student == null || course == null) return false;
		ArrayList<String> completedCoursesList = student.getCompletedCourses();
		ArrayList<String> preCourses = course.getPreCourses();
		return completedCoursesList.containsAll(preCourses); }}