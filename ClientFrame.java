package 服务器;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import 多人聊天室.ChatRoomClient;

import 多人聊天室.RSAUtils;

public class ClientFrame extends JFrame{
	private JPanel contentPane;
	private JLabel lblUsername;
	private JTextField tfMessage;
	private JButton btnSend;
	private JButton btnSend1;
	private JTextArea textArea;
	private String userName;
	ChatRoomClient client;
	public ClientFrame(String ip,String userName) {
		this.userName=userName;
		init();
		addListener();
		try {
			client=new ChatRoomClient(ip, 4569);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ReadMessageThread t =new ReadMessageThread();
		t.start();
	}
		private void init() {
			setTitle("客户端");
			setResizable(false);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setBounds(100,100,450,300);
			
			contentPane=new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPane.setLayout(null);
			setContentPane(contentPane);
			
			JScrollPane scrollPane=new JScrollPane();
			scrollPane.setBounds(5,5,534,229);
			contentPane.add(scrollPane);
			
			textArea=new JTextArea();
			scrollPane.setViewportView(textArea);
			textArea.setEditable(false);
			
			JPanel panel=new JPanel();
			panel.setBounds(5,235,534,32);
			contentPane.add(panel);
			panel.setLayout(null);
			lblUsername=new JLabel(userName);
			lblUsername.setHorizontalAlignment(SwingConstants.TRAILING);
			lblUsername.setBounds(2,4,55,22);
			panel.add(lblUsername);
			
			tfMessage=new JTextField();
			tfMessage.setBounds(62,5,230,22);
			tfMessage.setColumns(10);
			panel.add(tfMessage);
			
			btnSend=new JButton("发送");
			btnSend.setBounds(300,4,60,23);
			panel.add(btnSend);
			tfMessage.validate();
			
			btnSend1=new JButton("发送图片");
			btnSend1.setBounds(360,4,93,23);
			panel.add(btnSend1);

			
		}
	private void addListener() {
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Date date=new Date();
				SimpleDateFormat df=new SimpleDateFormat("yyyy年MM月dd日 HH：mm:ss");
				client.sendMessage1(userName+" "+df.format(date)+": \n"+tfMessage.getText());
				tfMessage.setText("");
				
			}
			
		});
		btnSend1.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent event)
			{
				JFileChooser chooser=new JFileChooser();
			    chooser.setCurrentDirectory(new File("."));
			    
				//show file chooser dialog
				int result =chooser.showOpenDialog(null);
				//if file selected,set it as icon of the label
				if(result==JFileChooser.APPROVE_OPTION)
				{
					String name=chooser.getSelectedFile().getPath();
					System.out.println(name);
					
					
				}
				
				
				
				
				
			}
		 
		});

		
		
		
		this.addWindowListener(new WindowAdapter() {
			
		public void windowClosing(WindowEvent atg0) {
				int op=JOptionPane.showConfirmDialog(ClientFrame.this,"确定要退出聊天室吗?","确定",JOptionPane.YES_NO_OPTION);
				if (op==JOptionPane.YES_OPTION) {
					client.sendMessage1("%EXIT%"+userName);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					client.close();
					System.exit(0);
				}
			
			}
		});
	}
	
	private class ReadMessageThread extends Thread{
		public void run() {
			while(true) {
				String str=client.receiveMessage();
				if(str.contains("publicKey")){
					client.receivepublicKey(str.substring(9));
					System.out.println(client.publicKey);
				}
				else if(str.contains("privateKey")){
					client.receiveprivateKey(str.substring(10));
					System.out.println(client.privateKey);
				}
				else{
					try {
						str = RSAUtils.privateDecrypt(str, RSAUtils.getPrivateKey(client.privateKey));
					} catch (NoSuchAlgorithmException e) {
						
						e.printStackTrace();
					} catch (InvalidKeySpecException e) {
						
						e.printStackTrace();
					}
					textArea.append(str+"\n");
				}
				
			}
		}
	}

	}
