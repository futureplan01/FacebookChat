/**
 * Have to test it out on different computers..
 */
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * The purpose of the Chat is to either try to connect with the friend 
 * or Handle the request from a friend to chat..
 * 
 * 
 * @author jesaiahprayor
 *
 */
public class TheChat extends Thread {
	String DefaultIP = "127.0.0.1";
	Socket Bud;
	String BudIP, type, myName; 
	JFrame frame;
	JPanel display;
	JTextArea History;
	JButton sendButton;
	JTextField sendInfo;
    BufferedReader in;
	PrintWriter out;
	
	
	public TheChat (Socket accepted,String userName){
		Bud = accepted;
		myName = userName;
		type = "Server";
		start();
	}
	public TheChat (String name, String IP){
		if(IP.contains(DefaultIP)){
			BudIP = DefaultIP;
		}
		else{		
			BudIP = IP;
		}
		myName = name;
		type = "Client";
		start();
	}
	/**
	 * First Thing It Does if it's requesting to talk to the Friend
	 * it makes the socket..
	 * 
	 * Then it makes the Gui...
	 *
	 */
	public synchronized void openStreams (){
		try {
			in = new BufferedReader (new InputStreamReader (Bud.getInputStream()));
			out = new PrintWriter (Bud.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run (){
		if(type.equals("Client")){
			try {
				System.out.println("Forming Socket");
				Bud = new Socket (BudIP,1500);
				System.out.println("Socket Formed");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		MakeGui ();
		openStreams ();
		while (true){
			try {
				String input = in.readLine ();
				if(input == null ){
					Bud.close();
				}else{
					History.append(input + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * Probably should make this a runnable class..
	 * @author jesaiahprayor
	 *
	 */
	class Sender extends Thread {
		public Sender (){
			start();
		}
		public void run (){
			SendText();
		}
		public synchronized void SendText(){
			try {
				out = new PrintWriter (Bud.getOutputStream());
				String input = sendInfo.getText();
				History.append(myName + ": " + input + "\n");
				out.println(myName + ": " + input );
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	public void MakeGui(){
		theHandler handles = new theHandler ();
		frame = new JFrame ("TALKING WITH THE BUD");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		display = new JPanel ();
		display.setLayout(new BorderLayout(3,3));
				
		//Top of the Gui
				
		History = new JTextArea ();
		History.setText("History \n");
		History.setEditable(false);
				
		display.add(History, BorderLayout.CENTER);
		
		sendButton = new JButton ("Send");
		sendButton.addActionListener(handles);
		sendInfo = new JTextField ();
				
		JPanel newPanel = new JPanel();
		newPanel.setLayout(new GridLayout (1,2));
				
		newPanel.add(sendInfo);
		newPanel.add(sendButton);
		
		display.add(newPanel, BorderLayout.PAGE_END);
		
		frame.add(display);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
	
	class theHandler implements ActionListener {
	
		public void actionPerformed(ActionEvent e) {
			// Should I create another Thread to send A text
			Sender send = new Sender ();
		}
		
	}
	
}
