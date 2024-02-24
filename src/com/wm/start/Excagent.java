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

public class Excagent extends Thread {
	ServerWindow hoserver;
	Socket socket;
	DataInputStream in ;
	DataOutputStream ou;
	public boolean flag = true;
	public Excagent (ServerWindow hoserver2,Socket socket) { 
	  try {
		this.hoserver= hoserver2;
		this.socket = socket;
		ou = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
		hoserver.verproper.ttime(this);
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
		Vector tempv = hoserver.onlineList;
		tempv.remove(this);
		hoserver.refreshlist();
	}
	public void sendMessageData(String text) {
		byte bytes[] = Encdoutils.hexStringToBytes(text);
		byte realdata[] = MessageDecodeUtil.encode(bytes, MessageDecodeUtil.getEncodeKey());
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