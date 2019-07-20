package ������;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public class ServerSocketFrame extends JFrame {
    private Image sendImg = null; // ����ͼ�����
    private Image receiveImg = null; // ����ͼ�����
    private SendImagePanel sendImagePanel = null; // ����ͼ��������
    private ReceiveImagePanel receiveImagePanel = null; // ����ͼ��������
    private File imgFile = null;// ������ѡ��ͼƬ��File����
    private JTextField tf_path;
    //private DataOutputStream out = null; // ����������
    private DataInputStream in = null; // ����������
    private ServerSocket server; // ����ServerSocket����
    //private Socket socket; // ����Socket����socket
    private long lengths = -1; // ͼƬ�ļ��Ĵ�С
    private HashSet<Socket> allSockets;
    public void getServer() {
        try {
            server = new ServerSocket(3978); // ʵ����Socket����
            while (true) { // ����׽���������״̬
            	Socket socket = server.accept(); // ʵ����Socket����
            	allSockets.add(socket);
            	System.out.println("�û��Ѿ�����������");
            	System.out.println(socket);
                //out = new DataOutputStream(socket.getOutputStream());// ������������
                in = new DataInputStream(socket.getInputStream());// �������������
                //getClientInfo(); // ����getClientInfo()����
                try {
                    long lengths = in.readLong();// ��ȡͼƬ�ļ��ĳ���
                    byte[] bt = new byte[(int) lengths];// �����ֽ�����
                    for (int i = 0; i < bt.length; i++) {
                        bt[i] = in.readByte();// ��ȡ�ֽ���Ϣ���洢���ֽ�����
                    }
                    receiveImg = new ImageIcon(bt).getImage();// ����ͼ�����
                    receiveImagePanel.repaint();// ���»���ͼ��
                } catch (Exception e) {
                } finally {
                    try {
                        if (in != null) {
                            in.close();// �ر���
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace(); // ����쳣��Ϣ
        }
    }
    /*
    private void getClientInfo() {
        try {
            long lengths = in.readLong();// ��ȡͼƬ�ļ��ĳ���
            byte[] bt = new byte[(int) lengths];// �����ֽ�����
            for (int i = 0; i < bt.length; i++) {
                bt[i] = in.readByte();// ��ȡ�ֽ���Ϣ���洢���ֽ�����
            }
            receiveImg = new ImageIcon(bt).getImage();// ����ͼ�����
            receiveImagePanel.repaint();// ���»���ͼ��
        } catch (Exception e) {
        } finally {
            try {
                if (in != null) {
                    in.close();// �ر���
                }
                if (socket != null) {
                    socket.close(); // �ر��׽���
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    */
    public static void main(String[] args) { // ������
        ServerSocketFrame frame = new ServerSocketFrame(); // �����������
        frame.setVisible(true);
        frame.getServer(); // ���÷���
    }
    
    public ServerSocketFrame() {
        super();
        allSockets=new HashSet<Socket>();
        setTitle("�������˳���");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 379, 260);
        
        final JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.NORTH);
        
        final JLabel label = new JLabel();
        label.setText("·����");
        panel.add(label);
        
        tf_path = new JTextField();
        tf_path.setPreferredSize(new Dimension(140, 25));
        panel.add(tf_path);
        
        sendImagePanel = new SendImagePanel();
        receiveImagePanel = new ReceiveImagePanel();
        final JButton button_1 = new JButton();
        button_1.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();// �����ļ�ѡ����
                FileFilter filter = new FileNameExtensionFilter(
                        "ͼ���ļ���JPG/GIF/BMP)", "JPG", "JPEG", "GIF", "BMP");// ����������
                fileChooser.setFileFilter(filter);// ���ù�����
                int flag = fileChooser.showOpenDialog(null);// ��ʾ�򿪶Ի���
                if (flag == JFileChooser.APPROVE_OPTION) {
                    imgFile = fileChooser.getSelectedFile(); // ��ȡѡ��ͼƬ��File����
                }
                if (imgFile != null) {
                    tf_path.setText(imgFile.getAbsolutePath());// ͼƬ����·��
                    try {
                        sendImg = ImageIO.read(imgFile);// ����BufferedImage����
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                sendImagePanel.repaint();// ����paint()����
            }
        });
        button_1.setText("ѡ��ͼƬ");
        panel.add(button_1);
        
        final JButton button = new JButton();
        button.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            	
            	
            	for(Socket s:allSockets) {
            		System.out.println(s);
                try {
                	DataOutputStream out= new DataOutputStream(s.getOutputStream());
                    DataInputStream inStream = null;// ������������������
                    if (imgFile != null) {
                        lengths = imgFile.length();// ���ѡ��ͼƬ�Ĵ�С
                        inStream = new DataInputStream(new FileInputStream(imgFile));// ��������������
                    } else {
                        JOptionPane.showMessageDialog(null, "��û��ѡ��ͼƬ�ļ���");
                        return;
                    }
                    out.writeLong(lengths);// ���ļ��Ĵ�Сд�������
                    byte[] bt = new byte[(int) lengths];// �����ֽ�����
                    int len = -1;
                    while ((len = inStream.read(bt)) != -1) {// ��ͼƬ�ļ���ȡ���ֽ�����
                        out.write(bt);// ���ֽ�����д�������
                    }
                    
                    out.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                
                
                
                
                
                
            	}
                
                
            }
        });
        button.setText("��  ��");
        panel.add(button);
        
        final JPanel panel_1 = new JPanel();
        panel_1.setLayout(new BorderLayout());
        getContentPane().add(panel_1, BorderLayout.CENTER);
        
        final JPanel panel_2 = new JPanel();
        panel_2.setLayout(new GridLayout(1, 0));
        final FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        panel_2.setLayout(flowLayout);
        panel_1.add(panel_2, BorderLayout.NORTH);
        
        final JLabel label_1 = new JLabel();
        label_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        label_1.setText("��������ѡ���Ҫ���͵�ͼƬ  ");
        panel_2.add(label_1);
        
        final JLabel label_2 = new JLabel();
        label_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        label_2.setText("���յ��ͻ��˷��͵�ͼƬ       ");
        panel_2.add(label_2);
        
        final JPanel imgPanel = new JPanel();
        final GridLayout gridLayout = new GridLayout(1, 0);
        gridLayout.setVgap(10);
        imgPanel.setLayout(gridLayout);
        panel_1.add(imgPanel, BorderLayout.CENTER);
        imgPanel.add(sendImagePanel);
        imgPanel.add(receiveImagePanel);
        sendImagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        receiveImagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
    }
    
    // ���������
    class SendImagePanel extends JPanel {
        public void paint(Graphics g) {
            if (sendImg != null) {
                g.clearRect(0, 0, this.getWidth(), this.getHeight());// �����ͼ�����ĵ�����
                g.drawImage(sendImg, 0, 0, this.getWidth(), this.getHeight(),
                        this);// ����ָ����С��ͼƬ
            }
        }
    }
    
    // ���������
    class ReceiveImagePanel extends JPanel {
        public void paint(Graphics g) {
            if (receiveImg != null) {
                g.clearRect(0, 0, this.getWidth(), this.getHeight());// �����ͼ�����ĵ�����
                g.drawImage(receiveImg, 0, 0, this.getWidth(),
                        this.getHeight(), this);// ����ָ����С��ͼƬ
            }
        }
    }
}
