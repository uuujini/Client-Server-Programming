import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server extends UnicastRemoteObject implements ServerIF {

	private static DataIF data;
    protected Server() throws RemoteException {
        super();
    }
    public static void main(String[] arg) throws NotBoundException { // Server가 프로세스가 되기 위해서 main 필요
        try {
            Server server = new Server();
            Naming.rebind("Server", server);
            System.out.println("Server is ready !!!");
            
            // Server가 RMI를 호출해야 하니까 여기서 Client 코드 가져오기. 
            data = (DataIF) Naming.lookup("Data");
            //System.out.println("Data's answer: " +data.getData());
        
        } catch(RemoteException e) {
            e.printStackTrace();
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
	@Override
	public ArrayList<Student> getAllStudentData() throws RemoteException {
		// TODO Auto-generated method stub
		return data.getAllStudentData();
	}
}
