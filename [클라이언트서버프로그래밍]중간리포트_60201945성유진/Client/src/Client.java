import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Client {
	private static TokenLogger tokenLogger;
	public static void main(String[] args) throws NotBoundException, IOException, DuplicateDataException,
			TokenTimeOutException, TokenDifferentException {
		ServerIF server;
		tokenLogger = new TokenLogger("TokenLog.log");
		BufferedReader objReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			server = (ServerIF) Naming.lookup("Server");
			boolean loggedIn = false;
			while (true) {
				if (!loggedIn) {
					printLoginMenu();
					String sChoice = objReader.readLine().trim();
					switch (sChoice) {
					case "1": // 1: SignUp User
						signForUser(server, objReader);
						break;
					case "2": // 2: Login
						loggedIn = loginUser(server, objReader);
						break;
					case "x":
						return;
					default:
						System.out.println("※ Invalid Choice !!! ※");}
				} else {
					printMainMenu();
					String sChoice = objReader.readLine().trim();
					switch (sChoice) {
					case "1": // 1: Show Student
						System.out.println("[ Student Info LIST ▼ ]");
						showList(server.getAllStudentData());
						break;
					case "2": // 2: Add Student
						addStudent(server, objReader);
						break;
					case "3": // 3: Delete Student
						deleteStudent(server, objReader);
						break;
					case "4": // 4: Show Course
						System.out.println("[ Course Info LIST ▼ ]");
						showList(server.getAllCourseData());
						break;
					case "5": // 5: Add Course
						addCourse(server, objReader);
						break;
					case "6": // 6: Delete Course
						deleteCourse(server, objReader);
						break;
					case "7": // 7: List Registers
						System.out.println("[ Registration Info LIST ▼ ]");
						showList(server.getAllRegisterData());
						break;
					case "8": // 8: Register Course
						registerCourse(server, objReader);
						break;
					case "9": // 9: Cancel Course
						cancelRegistration(server, objReader);
						break;
					case "x":
						tokenLogger.close();
						return;
					default:
						System.out.println(" ※ Invalid Choice !!! ※");}}}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NullDataException e) {
			e.printStackTrace();
			String actionMessage = e.doAction();
			if (actionMessage != null) {
				System.out.println(" ※ NULL 데이터 예외 발생: " + actionMessage + " ※");}}}
	private static void printLoginMenu() {
		System.out.println("------------- Main Menu -------------");
		System.out.println("1. Sign Up");
		System.out.println("2. Login");
		System.out.println("-------------------------------------");
		System.out.println("x. Exit");}
	private static void printMainMenu() {
		System.out.println("--------- Student Info Menu ---------");
		System.out.println("1. List Students");
		System.out.println("2. Add Student");
		System.out.println("3. Delete Student");
		System.out.println("---------- Course Info Menu ----------");
		System.out.println("4. List Courses");
		System.out.println("5. Add Course");
		System.out.println("6. Delete Course");
		System.out.println("--------- Register Info Menu ---------");
		System.out.println("7. List Registers");
		System.out.println("8. Register Course");
		System.out.println("9. Register Cancel");
		System.out.println("--------------------------------------");
		System.out.println("x. Exit");}
	private static void showList(ArrayList<?> dataList) {
		String list = "";
		for (int i = 0; i < dataList.size(); i++) {
			list += dataList.get(i) + "\n";}
		System.out.println(list);}
	private static boolean signForUser(ServerIF server, BufferedReader objReader)
			throws IOException, RemoteException, NullDataException {
		System.out.println("-------------------------------------------------");
		System.out.println("[ Sign Up :: 유저 토큰 발행을 위한 사용자 정보를 입력하세요. ]");
		System.out.print("Student ID: ");
		String studentId = objReader.readLine().trim();
		System.out.print("Password: ");
		String newPassword = objReader.readLine().trim();
		try {
			Token signSuccess = server.signForUser(studentId, newPassword);
			if (signSuccess != null) {
				System.out.println(" ※ 로그인 성공. 토큰 재발급에 어려움이 있으니, 보관에 유의하세요. ※");
				System.out.println(" ※ " + studentId + "님의 토큰: " + signSuccess.getToken() + " ※");
				tokenLogger.log("Token issued for student: " + studentId + " with token: " + signSuccess.getToken());
			} else { System.out.println(" ※ 학생 정보를 찾을 수 없습니다. ※");
			} return signSuccess != null;
		} catch (DuplicateDataException e) {
			String actionMessage = e.doAction();
			System.out.println(" ※ 중복 데이터 예외 발생: " + actionMessage + " ※");
			return false;}}
	private static boolean loginUser(ServerIF server, BufferedReader objReader) throws IOException, NullDataException,
			DuplicateDataException, TokenTimeOutException, TokenDifferentException {
		System.out.println("-------------------------------------");
		System.out.println("[ Login :: 로그인 정보를 입력하세요. ▼ ]");
		System.out.print("Student ID: ");
		String studentId = objReader.readLine().trim();
		System.out.print("Password: ");
		String studentPW = objReader.readLine().trim();
		System.out.print("User Token: ");
		String studentToken = objReader.readLine().trim();
		if (server.loginUser(studentId, studentPW, studentToken)) {
			System.out.println(" ※ Login[SUCCESS] :: 수강신청으로 진입합니다. ※"); return true;
		} else { System.out.println(" ※ Login[FAIL] :: User 등록 먼저 하세요. ※"); return false;}}
	private static void addStudent(ServerIF server, BufferedReader objReader)
			throws IOException, RemoteException, NullDataException, DuplicateDataException {
		System.out.println("[ 추가할 Student 정보를 입력하세요. ▼ ]");
		System.out.print("Student ID: ");
		String studentId = objReader.readLine().trim();
		System.out.print("Student Name: ");
		String studentName = objReader.readLine().trim();
		System.out.print("Student Department: ");
		String studentDept = objReader.readLine().trim();
		System.out.print("Student Completed Course List: ");
		String completedCourses = objReader.readLine().trim();
		if (server.addStudent(studentId + " " + studentName + " " + studentDept + " " + completedCourses))
			System.out.println(" ※ Add Student[SUCCESS] ※");
		else
			System.out.println(" ※ Add Student[FAIL] :: 중복 사용자 ID입니다. ※");}
	private static void deleteStudent(ServerIF server, BufferedReader objReader) throws RemoteException, IOException {
		System.out.println("[ 삭제할 Student 정보를 입력하세요. ▼ ]");
		System.out.print("Student ID: ");
		if (server.deleteStudent(objReader.readLine().trim()))
			System.out.println(" ※ Delete Student[SUCCESS] ※");
		else
			System.out.println(" ※ Delete Student[FAIL] :: 잘못된 사용자 ID입니다. ※");}
	private static void addCourse(ServerIF server, BufferedReader objReader)
			throws IOException, RemoteException, NullDataException, DuplicateDataException {
		System.out.println("[ 추가할 Course 정보를 입력하세요. ▼ ]");
		System.out.print("Course ID: ");
		String courseId = objReader.readLine().trim();
		System.out.print("Professor Name: ");
		String professorName = objReader.readLine().trim();
		System.out.print("Course Name: ");
		String courseName = objReader.readLine().trim();
		System.out.print("Course Completed Course List: ");
		String completedCourses = objReader.readLine().trim();
		if (server.addCourse(courseId + " " + professorName + " " + courseName + " " + completedCourses))
			System.out.println(" ※ Add Course[SUCCESS] ※");
		else
			System.out.println(" ※ Add Course[FAIL] :: 중복 강의 ID입니다 ※");}
	private static void deleteCourse(ServerIF server, BufferedReader objReader) throws RemoteException, IOException {
		System.out.println("[ 삭제할 Course 정보를 입력하세요. ▼ ]");
		System.out.print("Course ID: ");
		if (server.deleteCourse(objReader.readLine().trim()))
			System.out.println(" ※ Delete Course[SUCCESS] ※");
		else
			System.out.println(" ※ Delete Course[FAIL] :: 잘못된 강의 ID입니다. ※");}
	private static void registerCourse(ServerIF server, BufferedReader objReader)
			throws RemoteException, IOException, NullDataException, DuplicateDataException {
		System.out.println("[ 신청할 Registration 정보를 입력하세요. ▼ ]");
		System.out.print("Student ID: ");
		String studentId = objReader.readLine().trim();
		System.out.print("Course ID: ");
		String courseId = objReader.readLine().trim();
		LocalDateTime now = LocalDateTime.now();
		String registerDate = now.format(DateTimeFormatter.ofPattern("MM/dd-HH:mm"));
		if (server.registerCourse(studentId + " " + courseId + " " + registerDate + "[신청완료]"))
			System.out.println(" ※ Register Course[SUCCESS] ※");
		else
			System.out.println(" ※ Register Course[FAIL] :: 선제 강의를 먼저 수강하세요. ※");}
	private static void cancelRegistration(ServerIF server, BufferedReader objReader)
			throws RemoteException, IOException {
		System.out.println("[ 취소할 Registration 정보를 입력하세요. ▼ ]");
		System.out.print("studentId : ");
		if (server.cancelRegistration(objReader.readLine().trim()))
			System.out.println(" ※ Cancel Course[SUCCESS] ※");
		else
			System.out.println(" ※ Cancel Course[FAIL] :: 잘못된 수강 ID입니다. ※");}}