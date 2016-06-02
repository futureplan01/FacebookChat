import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TheServer {
	static ServerSocket Listener;
	static Socket client;
	static ArrayList <Socket> ServersConnection;
	static ArrayList <ClientHandler> myConnections;
	static ClientHandler handleClient;
	static HashMap ClientsID;
	static String userName;
	public static void ReadName(){
		BufferedReader in;
		try {
			in = new BufferedReader (new InputStreamReader(client.getInputStream()));
			userName = in.readLine();
			ClientsID.put(userName, client.getRemoteSocketAddress());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void SendInfo (){
		PrintWriter out;
		try {
			out = new PrintWriter (client.getOutputStream());
			int amount = ServersConnection.size();
			out.println(amount);
			
			if (amount > 0){
				Set entrySet =ClientsID.entrySet();
				Iterator it = entrySet.iterator();
				String Output = "";
				while (it.hasNext()){
					Map.Entry me = (Map.Entry)it.next();
					// Each Value Is Stored On a Seperate Line..
					Output += me.getKey() + "," + me.getValue() + "\n";
					// Sends Each Client's Info
				}
				
				out.println(Output);
				out.flush();
				
			}else{
				out.print(amount);
				out.flush();
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void main (String [] args){
		/**
		 * First Thing I do Is make the Server Socket
		 */
		try {
			Listener = new ServerSocket (1738);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/**
		 * Each Thread Communicate With A Client
		 * I need to store them inside an dynamic array if I want to make any changes to all of them
		 */
		ServersConnection = new ArrayList<Socket>();
		
		/**
		 * Use to Update the seperate Thread Connections
		 */
		myConnections = new ArrayList<ClientHandler>();
		
		/**
		 * I'm also going to create a HashMap in myServer So all of my clients Can have a list of 
		 * userNames and IP address of the rest of the people on my Server
		 */
		ClientsID = new HashMap ();
		
		/**
		 * Make a multiThreaded Server So any client come to our Server.
		 */
		while (true){
			try {
				client = Listener.accept();
				ServersConnection.add(client);
				/**
				 * In Order to get the UserName and I.D the client must first send it's User Name
				 * I can get it's IP address when the client accepts my Connection
				 */
				
					ReadName();
					SendInfo();
					try {
						/**
						 * Allows Thread To Get CPU Time
						 */
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	
				/**
				 * For Now the point of a new thread is to update the other clients..
				 */
				handleClient = new ClientHandler(client,ServersConnection,ClientsID);
				myConnections.add(handleClient);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(ServersConnection.size() > 1)
			handleClient.updateClients(myConnections,ClientsID);
		}
	}
}
/**
 * This Class will handle the connection between the Socket and the Thread
 * @author jesaiahprayor
 */
class ClientHandler extends Thread{
	Socket newClient;
	HashMap ClientsInfo;
	BufferedReader in;
	PrintWriter out;
	ArrayList <Socket> allConnections;
	public ClientHandler (Socket newClient, ArrayList <Socket> allConnections, HashMap ClientsID){
		this.newClient = newClient;
		this.allConnections = allConnections;
		ClientsInfo = ClientsID;
		start();
	}
	/**
	 * Updates the other clients to change their gui
	 */
	public synchronized void updateClients(ArrayList<ClientHandler> allClients, HashMap newInfo){
		int i = 0;
		while (i < allClients.size()){
			allClients.get(i).ClientsInfo = newInfo;
			allClients.get(i).sendInfo();
			i++;
		}
	}
	public synchronized void openStreams(){
		try {
			out = new PrintWriter (newClient.getOutputStream());			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Sends Data to Client so that it can see who's online..
	 */
	public synchronized void sendInfo (){
		PrintWriter out;
		try {
			out = new PrintWriter (newClient.getOutputStream());
			int amount = allConnections.size();
			out.println(amount);
			
			if (amount > 0){
				Set entrySet =ClientsInfo.entrySet();
				Iterator it = entrySet.iterator();
				String Output = "";
				while (it.hasNext()){
					Map.Entry me = (Map.Entry)it.next();
					// Each Value Is Stored On a Seperate Line..
					Output += me.getKey() + "," + me.getValue() + "\n";
					// Sends Each Client's Info
				}
				
				out.println(Output);
				out.flush();
				
			}else{
				out.print(amount);
				out.flush();
			 }
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	/**
	 * Point of this Thread... 
	 * is To wait for Any clients who logged out to be removed from the server...
	 * and then update the server 
	 * then update the rest of the users
	 */
	public void run (){
		while (true){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
