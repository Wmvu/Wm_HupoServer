package com.wm.start;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import utils.VerProper;

public class ServerWindow extends JFrame implements ActionListener{
	/**
	 *this is a important class and best main window every thing of it
	 *这是最主要的窗口 
	 */
	private static final long serialVersionUID = 1L;
	JButton start,stop,senior;
	JList<String> Useronlise;
	Vector<Excagent> onlineList = new Vector<Excagent>();
	JTextField jtfport,mcsrk;
	ServerSocket serversocket ;
	JLabel dkh,mc;
	static ByteBuffer byterbuffer = ByteBuffer.allocate(1024);	
	public DataChat datachat= new DataChat();
	public VerProper verproper;
	public ExcAl excal;
	public JPopupMenu jpmenu;
	public JMenuItem menuitem;
	public ServerWindow(String title) {
		super(title);
		ServerJF(title);
	}
	public void ServerJF(String title) {
		//创建组件
		dkh= new JLabel("端口号:");
		mc = new JLabel("服务器名称:");
		jtfport = new JTextField("6030");
		mcsrk = new JTextField("秽土转生");
		start = new JButton("启动");stop = new JButton("停止");senior = new JButton("高级");
		JPanel jp = new JPanel();
		Useronlise = new JList<String>();
		verproper = new VerProper();
		JScrollPane jsp = new JScrollPane(Useronlise);
		JSplitPane jspx = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jsp,jp);
		jpmenu = new JPopupMenu();
		menuitem = new JMenuItem("踢出");
		//窗口设定
		this.setSize(410, 300);
		this.setTitle(title);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		jp.setLayout(null);
		this.datachat.addElement("---=#以下为连接数据信息：#=---");
		//添加组件
		jp.add(start);jp.add(stop);jp.add(senior);
		start.setBounds(10,100,80,30);stop.setBounds(100,100,80,30);senior.setBounds(100, 220, 80, 30);
		jp.add(jtfport);jtfport.setBounds(120, 50, 50, 25);stop.setEnabled(false);
		jp.add(mcsrk);mcsrk.setBounds(110, 10, 70, 23);
		jp.add(dkh);dkh.setBounds(40, 50, 50, 25);
		jp.add(mc);mc.setBounds(20, 10, 73, 25);
		jpmenu.add(menuitem);
		Useronlise.add(jpmenu);
		this.add(jspx);
		jspx.setDividerLocation(200);
		jspx.setDividerSize(4);
		this.setVisible(true);	
		start.addActionListener(this);

		stop.addActionListener(this);
		senior.addActionListener(this);
		Useronlise.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 3) {
					jpmenu.show(Useronlise, e.getX(), e.getY());
				}
			}
		});
		menuitem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				kick(Useronlise.getSelectedIndex());
				
			}
		});
	}
public void senjorcj() {
	new SenjorFrame(this.getX()+this.getWidth(), this.getY()-50,this);
}
public void serve() {
	 int pork =Integer.parseInt(jtfport.getText().trim());
		try {
			verproper.initilise();//让配置项加载器初始化
			serversocket = new ServerSocket(pork);
			excal = new ExcAl(this);
			excal.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
}

public void refreshlist() {
	Vector<String> v = new Vector<String>();
	int size = this.onlineList.size();
	for (int i = 0;i<size;i++) {
		Excagent tempsat = this.onlineList.get(i);
		String tems = tempsat.socket.getInetAddress().toString();
		tems=tems + "|" + tempsat.getName();
		v.add(tems);
		
	}
	this.Useronlise.setListData(v);
}
@Override
public void actionPerformed(ActionEvent arg0) {
	if(arg0.getSource() == start) {
		serve();
		stop.setEnabled(true);
		start.setEnabled(false);
		jtfport.setEnabled(false);
		mcsrk.setEnabled(false);
		System.out.println("服务器启动成功");
	}else if(arg0.getSource() == stop) {
		for (Object object : onlineList) {
			Excagent exa = (Excagent) object;
			exa.stopExcagent();
		}
		try {
			excal.stopServer();
			serversocket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		stop.setEnabled(false);
		start.setEnabled(true);
		datachat.removeAllElements();
		this.datachat.addElement("---=#以下为连接数据信息：#=---");
		System.out.println("服务器正常关闭");
	}else if (arg0.getSource() == senior){
		senjorcj();
	}
	
}
public void kick(int n) {
	Excagent exc = this.onlineList.get(n);
	exc.stopExcagent();
}
}


