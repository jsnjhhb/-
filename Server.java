package 服务器;

import java.awt.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import javax.swing.*;



public class Server extends JFrame {
	private ServerSocket serverSocket;
	private HashSet<Socket> allSockets;

    private Image receiveImg = null; // 声明图像对
    long lengths =-1;
	byte[] bt=null;
	public Server() {
		super();
        allSockets=new HashSet<Socket>();
        try {
			serverSocket =new ServerSocket(3978);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	
	public void startService() throws IOException{
		while(true) {
			Socket s=serverSocket.accept();
			System.out.println("用户已经进入聊天室");
			System.out.println(s);
			allSockets.add(s);
			ServerThread t=new ServerThread(s);
			t.start();
		}
	}
	
	
	private class ServerThread extends Thread{
		Socket socket;
		public ServerThread(Socket socket) {
			this.socket=socket;
		}
		public void run() {
		DataInputStream in = null;// 获得输入流对象
		try {
			in = new DataInputStream(socket.getInputStream());
			while(true) {
				try {
                    lengths = in.readLong();// 读取图片文件的长度
                    bt = new byte[(int) lengths];// 创建字节数组
                    for (int i = 0; i < bt.length; i++) {
                        bt[i] = in.readByte();// 读取字节信息并存储到字节数组
                    }
                    receiveImg = new ImageIcon(bt).getImage();// 创建图像对象
                    
                } catch (Exception e) {
                } finally {
                	sendMessageTOALLClient(receiveImg);
                }
				
			}//while
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
		public void sendMessageTOALLClient(Image img)throws IOException {
			DataOutputStream out = new DataOutputStream(null);
			for(Socket s:allSockets) {
				
				try {
                	out= new DataOutputStream(s.getOutputStream());
                    out.writeLong(lengths);// 将文件的大小写入输出流
                    byte[] bt2=bt;
                    for (int i = 0; i < bt2.length; i++) {// 将图片文件读取到字节数组
                        out.write(bt2);// 将字节数组写入输出流
                    }
                    
                    out.flush();
                } catch (IOException e1) {
                	e1.printStackTrace();
                }
			}
			out.close();
			allSockets.removeAll(allSockets);
		}
	}

	
	public static void main(String[] arg0) {
		Server server=new Server();
		server.setVisible(true);
		try {
			server.startService();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
}
