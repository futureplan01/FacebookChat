import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;





public class TheClient {
	static JFrame frame;
	static JPanel display;
	static JButton Connect;
	static JTextField showIP;
	static JTextArea homeScreen;
	static String userName, DefaultIP = "127.0.0.1";
	JPanel sidePanel;
	HashMap dataBase;
	
	/**
	 * The Gui is made inside the constructor
	 * Will store and show everyone connected to the server
	 * 
	 */
	
	public TheClient (){
		/**
		 * Gets the user Name so that clients can tell them apart..
		 */
		userName = getUserName ();
		frame = new JFrame(userName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		handlesButtons handles = new handlesButtons ();
		display = new JPanel ();
		display.setLayout(new BorderLayout (3,3));
		
		JPanel topDisplay = new JPanel ();
		topDisplay.setLayout(new GridLayout (1,2));
		/**
		 * After Pressing this button the user will log onto the server
		 */
		sidePanel = new JPanel ();
		Connect = new JButton ("Log On");
		Connect.addActionListener(handles);
		showIP = new JTextField ();
		showIP.setText(DefaultIP);
		topDisplay.add(Connect);
		topDisplay.add(showIP);
		display.add(topDisplay, BorderLayout.NORTH);
		
		homeScreen = new JTextArea ();
		homeScreen.setText("First Log On To Server..");
		homeScreen.setEditable(false);
		display.add(homeScreen, BorderLayout.CENTER);
		
		frame.add(display);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
	public String getUserName (){
		String name = JOptionPane.showInputDialog("Please give me a user name");
		while (name == null ){
			name = JOptionPane.showInputDialog("Please give me a user name");
		}
		return name;
	}
	class handlesButtons implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			String whatAction = event.getActionCommand();
			
			if(whatAction.equals("Log On")){ // Connects With Server...
				/**
				 * Will get the IP address of any element inside the JTextField...
				 */
				ConnectToServer Connection = new ConnectToServer (userName,showIP.getText());
			}
			/**
			 * Other Buttons are only JButtons to open a connection..
			 */
			else{
				TheChat newBud = new TheChat (userName , (String)dataBase.get(whatAction) );
			}
		}
	}
	
	public static void main (String [] args){
		TheClient myBud = new TheClient();
	}
	class ConnectToServer extends Thread {
		String userName;
		String IP;
		Socket client;
		BufferedReader in;
		PrintWriter out;
		
		public ConnectToServer (String userName, String IP){
			this.userName = userName;
			this.IP = IP;
			start ();
		}
		public synchronized void SendName (){
			try {
				out = new PrintWriter(client.getOutputStream());
				out.println(userName);
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public synchronized void ReadInfo(){
			handlesButtons handles = new handlesButtons ();
			// First I will recieve the amount of Other Clients
			while(true){
			/**
			 * Have to Fix the Buttons...
			 */
			try {
				in = new BufferedReader (new InputStreamReader(client.getInputStream()));
				String amount = in.readLine (); 
				/**
				 * Should Stall here until given more instructions
				 */
				System.out.println("Number Of Clients: " + amount);
				int number = Integer.parseInt(amount);
				if (number == 1){
					// I'm the only one on so I can't do anything..
				}else{
					sidePanel.removeAll();
					sidePanel.setLayout(new GridLayout (10,1));
					int i = 0;
					while (i < number){
						String input = in.readLine();
						System.out.println("What the Server is giving me: " + input);
						String [] info = input.split(",/");
						
						if(!info[0].equals(userName)){
							dataBase.put(info[0], info[1]);
							JButton Button = new JButton (info[0]);
							Button.addActionListener(handles);
							sidePanel.add(Button);
						}
						i++;
					}
					display.add(sidePanel, BorderLayout.EAST);
					frame.pack();
					frame.setSize(500, 500);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
				e.printStackTrace();
				}
			}
		}
		public synchronized void OpenStreams(){
			try {
				out = new PrintWriter (client.getOutputStream());
				dataBase = new HashMap();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void run(){
			try {
				client = new Socket ("127.0.0.1",1738);
				homeScreen.append("\n HAVE FUN CHATTING \n");
				Connect.setEnabled(false);
				/**
				 * First send userName
				 */
				SendName();
				/**
				 * OpenStreams
				 */
				OpenStreams();
				/**
				 * First make a new Thread to wait for Friends to contact me...
				 */
				ActiveListener waitForFriend = new ActiveListener (userName);
				/**
				 * I have to Constantly read info In Case of new clients arrival
				 */
				ReadInfo();
				
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

}
/**
 * Still Need To Make A Seperate Class for the server Socket.....
 * Figure out why the buttons aren't working right...
 * Make sure we're able to use the GUIChat Correctly
 */
class ActiveListener extends Thread {
	Socket Friend;
	String myName;
	public ActiveListener (String userName){
		myName = userName;
		start();
	}
	/**
	 * ServerSocket 
	 */
	public void run (){
		try {
			ServerSocket waitForBud = new ServerSocket (1500);
			while (true){
				Friend = waitForBud.accept();
				TheChat newFriend = new TheChat (Friend, myName);
				try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}




