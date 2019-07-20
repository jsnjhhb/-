package 服务器;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import 多人聊天室.ChatRoomServer;

public class LinkServerFrame extends JFrame{
	private JTextField tfIP;
	private JTextField tfUserName;
	private JButton btnLink;
	private JPanel contentPane;
	private JLabel lblIP;
	private JLabel lblUserName;
	public LinkServerFrame() {
		setTitle("连接服务器");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100,100,390,150);
		contentPane=new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		lblIP=new JLabel("服务器IP地址:");
		lblIP.setFont(new Font("微软雅黑",Font.PLAIN,14));
		lblIP.setBounds(20,15,100,15);
		contentPane.add(lblIP);
		
		tfIP=new JTextField("127.0.0.1");
		tfIP.setBounds(121,13,242,21);
		contentPane.add(tfIP);
		tfIP.setColumns(10);
		
		lblUserName=new JLabel("用户名:");
		lblUserName.setFont(new Font("微软雅黑",Font.PLAIN,14));
		lblUserName.setBounds(60,40,60,15);
		contentPane.add(lblUserName);
		
		tfUserName=new JTextField();
		tfUserName.setBounds(121,42,242,21);
		contentPane.add(tfUserName);
		tfUserName.setColumns(10);
		
		btnLink=new JButton("连接服务器");
		btnLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_btnLink_actionPerformed(e);
			}
		});
		btnLink.setFont(new Font("微软雅黑",Font.PLAIN,14));
		btnLink.setBounds(140,80,120,23);
		contentPane.add(btnLink);
	}
		
		
	
	
	protected void do_btnLink_actionPerformed(ActionEvent e) {
		if(!tfIP.getText().equals("")&&!tfUserName.getText().equals("")) {
			dispose();
			ClientFrame clientFrame=new ClientFrame(tfIP.getText().trim(),tfUserName.getText().trim());
			clientFrame.setVisible(true);
			
		}else {
			JOptionPane.showMessageDialog(null, "文本框内的内容不为空", "警告",JOptionPane.WARNING_MESSAGE);
			
		}
	}
	public static void main(String[] args) {
		LinkServerFrame linkServerFrame=new LinkServerFrame();
		linkServerFrame.setVisible(true);
		
	}
	

}
