package 多人聊天室;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import 服务器.ServerSocketFrame;

public class ChatRoomServer {
	
	private ServerSocket serverSocket;
	private static HashSet<Socket> allSockets;
	Map<String, String> keyMap = RSAUtils.createKeys(512);
	Map<Socket,String> publicKeyMap = new HashMap<Socket, String>(); 
	public String publicKeyServer = keyMap.get("publicKey");
	public String privateKeyServer = keyMap.get("privateKey");
	public ChatRoomServer() {
		try {
			serverSocket=new ServerSocket(4569);
		}catch(IOException e){
			e.printStackTrace();
		}
		allSockets=new HashSet<Socket>();
		//System.out.println(publicKey);
		//System.out.println(privateKey);
	}
	public void startService() throws IOException{
		while(true) {
			Socket s=serverSocket.accept();
			BufferedWriter bw=new BufferedWriter(new OutputStreamWriter((s.getOutputStream())));
			Map<String, String> keyMap1 = RSAUtils.createKeys(512);
			StringBuilder b = new StringBuilder();
			b.append("publicKey").append(publicKeyServer);
			System.out.println("服务公"+publicKeyServer);
			publicKeyMap.put(s, keyMap1.get("publicKey"));
			System.out.println("客户公"+keyMap1.get("publicKey"));
			bw.write(b.toString());
			bw.newLine();
			bw.flush();
			StringBuilder c = new StringBuilder();
			c.append("privateKey").append(keyMap1.get("privateKey"));
			System.out.println("客户私"+keyMap1.get("privateKey"));
			bw.write(c.toString());
			bw.newLine();
			bw.flush();
			
			System.out.println("用户已经进入聊天室");
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
		BufferedReader br=null;
		try {
			br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true) {
				String str=br.readLine();
				try {
					str = RSAUtils.privateDecrypt(str, RSAUtils.getPrivateKey(privateKeyServer));
				} catch (NoSuchAlgorithmException e1) {

					e1.printStackTrace();
				} catch (InvalidKeySpecException e1) {

					e1.printStackTrace();
				}
				if(str.contains("Exit")) {
					
					sendMessageTOALLClient(str.split(":")[1]+" 用户已退出聊天室");
					allSockets.remove(socket);
					socket.close();
					return;
				}
				sendMessageTOALLClient(str);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
		public void sendMessageTOALLClient(String message)throws IOException {
			String str = null;
			for(Socket s:allSockets) {
				try {
				BufferedWriter bw=new BufferedWriter(new OutputStreamWriter((s.getOutputStream())));
				String gongyao = publicKeyMap.get(s);
				System.out.println("客户公"+gongyao);
				try {
					str = RSAUtils.publicEncrypt(message, RSAUtils.getPublicKey(gongyao));
					System.out.println("加密后信息"+str);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
				bw.write(str);
				bw.newLine();
				bw.flush();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	public static void main(String[] args) {
		
		try {
			new ChatRoomServer().startService();
		}catch (IOException e){
			e.printStackTrace();
		}
		ServerSocketFrame frame = new ServerSocketFrame(); // 创建本类对象
        frame.setVisible(true);
        frame.getServer(); // 调用方法
	}
	public static HashSet<Socket> getAllSockets() {
		return allSockets;
	}
	public static void setAllSockets(HashSet<Socket> allSockets) {
		ChatRoomServer.allSockets = allSockets;
	}
	
}
