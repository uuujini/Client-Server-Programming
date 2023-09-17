import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    public static void main(String[] args) throws NotBoundException, IOException {
    	ServerIF server;
    	BufferedReader objReader = new BufferedReader(new InputStreamReader(System.in));
        try {
        	server = (ServerIF) Naming.lookup("Server"); // ServerIF로 typecasting을 해주어서 "AddServer"를 찾아오게끔 한다
            System.out.println("******************* MENU *******************");
            System.out.println("1. List Students");
            System.out.println("2. List Courses");
            
            String sChoice = objReader.readLine().trim();
            // list 시각화 신경쓰기 가독성 있도록
            if (sChoice.equals("1")) {
            	System.out.println("Server's answer: " + server.getAllStudentData());
            }
            else if (sChoice.equals("2")) {
            	System.out.println("Homework !!! ");
            }
            // homework 
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
