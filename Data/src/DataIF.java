import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface DataIF extends Remote {
    ArrayList<Student> getAllStudentData () throws RemoteException;
}
