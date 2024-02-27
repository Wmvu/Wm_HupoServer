package com.wm.start;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Vector;

import com.gowild.dao.SocketMessage;
import com.gowild.dao.UserKey;

import utils.Encdoutils;
import utils.MessageDecodeUtil;
import utils.Proxy;

public class Excagent extends Thread {
	ServerWindow hoserver;
	Socket socket;
	DataInputStream in ;
	DataOutputStream ou;
	public boolean flag = true;
	private UserKey userkey;
	Proxy px;
	public Excagent (ServerWindow hoserver2,Socket socket) { 
	  try {
		this.hoserver= hoserver2;
		this.socket = socket;
		ou = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
		userkey = new UserKey();
		hoserver.verproper.ttime(this);
	} catch (IOException e) {
		e.printStackTrace();
	}
	 
	}
	public Proxy getPx() {
		return px;
	}
	public void setPx(Proxy px) {
		this.px = px;
	}
	public void run () {
		while (flag) {
			try {
				byte[] msg = new byte[2048];
				int a =in.read(msg);
				byte[] msgdata = Arrays.copyOf(msg, a);
				byte[] cleardata = receive(msgdata);
				if(px != null)px.send(cleardata);
				this.hoserver.datachat.addElement(Encdoutils.bytesToHexString(cleardata));
				SocketMessage smg =	SocketMessage.obtain().parseData(cleardata);
				if(hoserver.verproper.getcodelist().contains(Integer.valueOf(smg.getCode()))) {
					this.sendMessageData(hoserver.verproper.getPerptitem(Integer.valueOf(smg.getCode())));
					}
			} catch (Exception e) {
				try {
					flag = false;
					ou.close();
					in.close();
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				delete_client();
			}
		}
	}

	public void delete_client() {
		Vector<Excagent> tempv = hoserver.onlineList;
		tempv.remove(this);
		hoserver.refreshlist();
	}
	public byte[]receive(byte[] msgdata) {
		byte[] cleardata = MessageDecodeUtil.decode(msgdata, this.userkey.getDekey());
		return cleardata;
		
	}
	public void sendMessageData(String text) {
		byte bytes[] = Encdoutils.hexStringToBytes(text);
		this.sendMessageData(bytes);
	}
	public void sendMessageData(byte[] bytes) {
		byte realdata[] = MessageDecodeUtil.encode(bytes, this.userkey.getEnkey());
		try {
			this.ou.write(realdata);
			this.ou.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void stopExcagent() {
		flag = false;
		delete_client();
		try {
			this.socket.shutdownOutput();
			this.socket.shutdownInput();
			if(socket != null)socket.close();
			socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}