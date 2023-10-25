import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAO {
	private Connection DBConnection;
	private SaltGenerator saltGenerator;
	public DAO(String dbURL, String dbUser, String dbPassword) throws ClassNotFoundException, SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			DBConnection = DriverManager.getConnection(dbURL, dbUser, dbPassword);
			saltGenerator = new SaltGenerator();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();}}
	public boolean addUser(String studentId, String newPassword, String token) throws RemoteException, NullDataException, DuplicateDataException {
		try {	
			String query = "SELECT COUNT(*) FROM users WHERE id = ?";
			PreparedStatement checkStatement = DBConnection.prepareStatement(query);
			checkStatement.setString(1, studentId);
			ResultSet resultSet = checkStatement.executeQuery();
			resultSet.next();
			int count = resultSet.getInt(1);
			resultSet.close();
			checkStatement.close();
			if (count > 0) { return false;}
			String salt = saltGenerator.generateSalt();
			String hashedPassword = saltGenerator.hashPassword(newPassword, salt);
			String insertStatement = "INSERT INTO users (id, password, salt, pwSalt, token) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = DBConnection.prepareStatement(insertStatement);
			preparedStatement.setString(1, studentId);
			preparedStatement.setString(2, newPassword);
			preparedStatement.setString(3, salt);
			preparedStatement.setString(4, hashedPassword);
			preparedStatement.setString(5, token);
			int rowsInserted = preparedStatement.executeUpdate();
			preparedStatement.close();
			return rowsInserted > 0;
		} catch (SQLException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;}}
	public boolean readUser(String userId, String password, String token) throws RemoteException {
		try {
			String query = "SELECT password, token FROM users WHERE id = ?";
			PreparedStatement statement = DBConnection.prepareStatement(query);
			statement.setString(1, userId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				String storedPassword = resultSet.getString("password");
				String storedToken = resultSet.getString("token");
				if (storedPassword.equals(password) && storedToken.equals(token)) {return true;}
			} return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;}}
	public ArrayList<Student> getAllStudentData() throws RemoteException, NullDataException {
		ArrayList<Student> students = new ArrayList<>();
		try {
			Statement statement = DBConnection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM students");
			while (resultSet.next()) {
				String studentInfo = resultSet.getString("studentId") + " " + resultSet.getString("name") + " "
				+ resultSet.getString("department") + " " + resultSet.getString("completedCoursesList");
				students.add(new Student(studentInfo));}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();}
		if (students.isEmpty()) { throw new NullDataException("No student data found.");} 
		return students;}
	public ArrayList<Course> getAllCourseData() throws RemoteException, NullDataException {
		ArrayList<Course> courses = new ArrayList<>();
		try {
			Statement statement = DBConnection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM courses");
			while (resultSet.next()) {
				String courseInfo = resultSet.getString("courseId") + " " + resultSet.getString("professorName") + " "
			    + resultSet.getString("courseName") + " " + resultSet.getString("preCourses");
				courses.add(new Course(courseInfo));}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} if (courses.isEmpty()) {throw new NullDataException("No course data found.");} return courses;}
	public ArrayList<Register> getAllRegisterData() throws RemoteException, NullDataException {
		ArrayList<Register> registrations = new ArrayList<>();
		try {
			Statement statement = DBConnection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM registrations");
			while (resultSet.next()) {
			String registrationInfo = resultSet.getString("studentId") + " " + resultSet.getString("courseId") + " "
				+ resultSet.getString("registerDate");
				registrations.add(new Register(registrationInfo));}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} if (registrations.isEmpty()) { throw new NullDataException("NO Registration data found.");}
		return registrations;}
	public boolean addStudent(String studentInfo) throws RemoteException, NullDataException, DuplicateDataException {
		try {
			Student student = new Student(studentInfo);
			for (Student existingStudent : getAllStudentData()) {
				if (existingStudent.match(student.studentId)) {throw new DuplicateDataException("Student with the same ID already exists.");}}
			String insertStatement = "INSERT INTO students (studentId, name, department, completedCoursesList) VALUES (?, ?, ?, ?)";
			PreparedStatement preparedStatement = DBConnection.prepareStatement(insertStatement);
			preparedStatement.setString(1, student.studentId);
			preparedStatement.setString(2, student.name);
			preparedStatement.setString(3, student.department);
			preparedStatement.setString(4, String.join(" ", student.completedCoursesList)); // Join the courses list
			preparedStatement.executeUpdate();
			preparedStatement.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Add Student is Failed !!");
			return false;}}
	public boolean deleteStudent(String studentId) throws RemoteException {
		try {
			String deleteStatement = "DELETE FROM students WHERE studentId = ?";
			PreparedStatement preparedStatement = DBConnection.prepareStatement(deleteStatement);
			preparedStatement.setString(1, studentId);
			int rowsDeleted = preparedStatement.executeUpdate();
			preparedStatement.close();
			if (rowsDeleted > 0) { return true;
			} else { return false;}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;}}
	public boolean addCourse(String courseInfo) throws RemoteException, NullDataException {
		try {
			Course course = new Course(courseInfo);
			for (Course existingCourse : getAllCourseData()) {
				if (existingCourse.match(course.courseId)) {
					System.out.println("Course with the same ID already exists.");
					return false;}}
			String insertStatement = "INSERT INTO courses (courseId, professorName, courseName, preCourses) VALUES (?, ?, ?, ?)";
			PreparedStatement preparedStatement = DBConnection.prepareStatement(insertStatement);
			preparedStatement.setString(1, course.courseId);
			preparedStatement.setString(2, course.professorName);
			preparedStatement.setString(3, course.courseName);
			preparedStatement.setString(4, String.join(" ", course.preCourses));
			preparedStatement.executeUpdate();
			preparedStatement.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;}}
	public boolean deleteCourse(String courseId) throws RemoteException {
		try {
			String deleteStatement = "DELETE FROM courses WHERE courseId = ?";
			PreparedStatement preparedStatement = DBConnection.prepareStatement(deleteStatement);
			preparedStatement.setString(1, courseId);
			int rowsDeleted = preparedStatement.executeUpdate();
			preparedStatement.close();
			if (rowsDeleted > 0) {
				return true;
			} else { return false;}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;}}
	public boolean registerCourse(String registerInfo) throws RemoteException, NullDataException {
		try {
			Register register = new Register(registerInfo);
			String insertStatement = "INSERT INTO registrations (studentId, courseId, registerDate) VALUES (?, ?, ?)";
			PreparedStatement preparedStatement = DBConnection.prepareStatement(insertStatement);
			preparedStatement.setString(1, register.studentId);
			preparedStatement.setString(2, register.courseId);
			preparedStatement.setString(3, register.registerDate);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			System.out.println("Course registration is successful!");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Course registration failed.");
			return false;}}
	public boolean cancelRegistration(String studentId) throws RemoteException {
		try {
			String deleteStatement = "DELETE FROM registrations WHERE studentId = ?";
			PreparedStatement preparedStatement = DBConnection.prepareStatement(deleteStatement);
			preparedStatement.setString(1, studentId);
			int rowsDeleted = preparedStatement.executeUpdate();
			preparedStatement.close();
			if (rowsDeleted > 0) { return true;
			} else { return false;}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;}}}