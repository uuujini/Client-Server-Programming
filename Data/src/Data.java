import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Data extends UnicastRemoteObject implements DataIF {

	protected static StudentList studentList;
    protected Data() throws RemoteException {
        super();
    }
    public static void main(String[] arg) throws FileNotFoundException, IOException{ // Server가 프로세스가 되기 위해서 main 필요
        try {
        	//System.setProperty("java.rmi.server.codebase", "file:/C:/Users/성유진/eclipse-workspace/Server/bin/");

        	Data data = new Data();
            Naming.rebind("Data", data);
            System.out.println("Data is ready !!!");
            
            studentList = new StudentList("Students.txt");
            
        } catch(RemoteException e) {
            e.printStackTrace();
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
    }
	@Override
	public ArrayList<Student> getAllStudentData() throws RemoteException {
		// TODO Auto-generated method stub
		return studentList.getAllStudentRecords();
	}
}
