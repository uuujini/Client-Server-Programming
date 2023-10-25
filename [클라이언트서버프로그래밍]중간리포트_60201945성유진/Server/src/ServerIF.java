import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
public interface ServerIF extends Remote {
    ArrayList<Student> getAllStudentData() throws RemoteException, NullDataException;
    ArrayList<Course> getAllCourseData() throws RemoteException, NullDataException;
    ArrayList<Register> getAllRegisterData() throws RemoteException, NullDataException;
    boolean addStudent(String studentInfo) throws RemoteException, NullDataException, DuplicateDataException;
    boolean deleteStudent(String studentId) throws RemoteException;
    boolean addCourse(String courseInfo) throws RemoteException, NullDataException, DuplicateDataException;
    boolean deleteCourse(String courseId) throws RemoteException;
    boolean registerCourse(String registerInfo) throws RemoteException, NullDataException, DuplicateDataException;
    boolean cancelRegistration(String studentId) throws RemoteException;
	boolean loginUser(String studentId, String password, String token) throws RemoteException, NullDataException, TokenDifferentException, TokenTimeOutException;
	Token signForUser(String studentId, String password) throws RemoteException, NullDataException, DuplicateDataException;}