package 多人聊天室;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ChatRoomClient {
	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;
	public String publicKey ;
	public String privateKey ;
	public ChatRoomClient(String host,int port) throws UnknownHostException,IOException{
		socket=new Socket(host,port);
		br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
	}
	public void sendMessage(String str) {
		try {
		
		bw.write(str);
		bw.newLine();
		bw.flush();
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	
	public void sendMessage1(String str) {
		try {
		try {
			str = RSAUtils.publicEncrypt(str, RSAUtils.getPublicKey(publicKey));
			System.out.println("客户使用公"+publicKey);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		bw.write(str);
		bw.newLine();
		bw.flush();
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	public String receiveMessage1() {
			String message=null;
		try {
			try {
				message = RSAUtils.privateDecrypt(br.readLine(), RSAUtils.getPrivateKey(privateKey));
				System.out.println("客户使用私"+privateKey);
				System.out.println("解密后"+message);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return message;
		
	}
	public String receiveMessage() {
		String message=null;
	try {
		
			message = br.readLine();
		
	}catch(IOException e){
		e.printStackTrace();
	}
	return message;
	
}
	public void receivepublicKey(String message){
        this.publicKey=message;
	}
	public void  receiveprivateKey(String message){
		this.privateKey = message;
	}
	
	public void close(){
		try {socket.close();
	}catch(IOException e) {
		e.printStackTrace();
	}
	}
}
