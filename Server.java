package ������;

import java.awt.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import javax.swing.*;



public class Server extends JFrame {
	private ServerSocket serverSocket;
	private HashSet<Socket> allSockets;

    private Image receiveImg = null; // ����ͼ���
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
			System.out.println("�û��Ѿ�����������");
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
		DataInputStream in = null;// �������������
		try {
			in = new DataInputStream(socket.getInputStream());
			while(true) {
				try {
                    lengths = in.readLong();// ��ȡͼƬ�ļ��ĳ���
                    bt = new byte[(int) lengths];// �����ֽ�����
                    for (int i = 0; i < bt.length; i++) {
                        bt[i] = in.readByte();// ��ȡ�ֽ���Ϣ���洢���ֽ�����
                    }
                    receiveImg = new ImageIcon(bt).getImage();// ����ͼ�����
                    
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
                    out.writeLong(lengths);// ���ļ��Ĵ�Сд�������
                    byte[] bt2=bt;
                    for (int i = 0; i < bt2.length; i++) {// ��ͼƬ�ļ���ȡ���ֽ�����
                        out.write(bt2);// ���ֽ�����д�������
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
