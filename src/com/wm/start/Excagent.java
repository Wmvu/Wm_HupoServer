package com.wm.start;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import com.gowild.dao.SocketMessage;

import utils.Encdoutils;
import utils.MessageDecodeUtil;

class Excagent extends Thread {
	ServerWindow hoserver;
	Socket socket;
	DataInputStream in ;
	DataOutputStream ou;
	boolean flag = true;
	public Excagent (ServerWindow hoserver2,Socket socket) { 
	  try {
		this.hoserver= hoserver2;
		this.socket = socket;
		ou = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
	} catch (IOException e) {
		e.printStackTrace();
	}
	 
	}
	public void run () {
		while (flag) {
			try {
				byte[] msg = new byte[2048];
				int a =in.read(msg);
				byte[] msgdata = Arrays.copyOf(msg, a);
				byte[] cleardata = MessageDecodeUtil.decode(msgdata, MessageDecodeUtil.getDecodeKey());
				this.hoserver.datachat.addElement(Encdoutils.bytesToHexString(cleardata));
				
				SocketMessage smg =	SocketMessage.obtain().parseData(cleardata);
				String path = System.getProperty("user.dir");
//				System.out.println(path);
				Properties prt = new Properties();
				BufferedReader inStream = new BufferedReader(new FileReader(path+"\\response.ini"));
				prt.load(inStream);
				Set<Object> list = prt.keySet();
				List<Object>arlist = new ArrayList<Object>(list);
//				System.out.println(arlist.toString());
				System.out.println(prt.getProperty(String.valueOf(smg.getCode())));
				System.out.println(smg.getCode());
				System.out.println(arlist.get(0));
				if(arlist.contains(String.valueOf(smg.getCode()))) {
//					System.out.println(String.valueOf(smg.getCode()));
					System.out.println(prt.getProperty(String.valueOf(smg.getCode())));
					byte bytes[] = Encdoutils.hexStringToBytes(prt.getProperty(String.valueOf(smg.getCode())));
					byte realdata[] = MessageDecodeUtil.encode(bytes, MessageDecodeUtil.getEncodeKey());
					this.ou.write(realdata);
					this.ou.flush();
					}
			} catch (Exception e) {
				try {
					ou.close();
					in.close();
					socket.close();
					flag = false;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				delete_client();
			}
		}
	}

	public void delete_client() {
		Vector tempv = hoserver.onlineList;
		tempv.remove(this);
		hoserver.refreshlist();
	}
	public void start_xyx() {
	}
	public void zf_ltgc(String msg) {
		Vector tempv = hoserver.onlineList;
		int size = tempv.size();
		msg = this.getName()+":"+msg;
		hoserver.datachat.addElement(msg);
	}
}