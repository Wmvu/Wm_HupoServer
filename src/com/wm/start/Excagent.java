package com.wm.start;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Vector;

import com.gowild.dao.SocketMessage;
import com.gowild.dao.UserKey;

import utils.Encdoutils;
import utils.MessageDecode;
import utils.MessageDecodeUtil;
import utils.Proxy;

public class Excagent extends Thread {
	ServerWindow hoserver;
	SocketChannel socket;
	public boolean flag = true;
	private UserKey userkey;
	Proxy px;
	ByteBuffer buf;
	Thread heart;
	public Excagent (ServerWindow hoserver2,SocketChannel socket) { 
	  this.hoserver= hoserver2;
	  this.socket = socket;
	  userkey = new UserKey();
	  hoserver.verproper.ttime(this);
	  buf = ByteBuffer.allocate(8192);
	 
	}
	public Proxy getPx() {
		return px;
	}
	public void setPx(Proxy px) {
		this.px = px;
	}
	public void run () {
		while (this.flag) {
			try {
				//接收
				buf.clear();
				int n = socket.read(buf);
				if(n == -1)this.stopExcagent();
				buf.flip();
				
				while (buf.limit()-buf.position()>6)on_pack();
//				buf.compact();
			
			} catch (Exception e) {
				e.printStackTrace();
				delete_client();
				this.stopExcagent();
				break;
			}
		}
	}
	private void on_pack() throws IOException {
		int len = MessageDecode.doDecode(buf, this.userkey);
		if(len == 0 )return;
		byte[] msgdata = new byte[len];
		buf.get(msgdata);
		
		if(ServerWindow.isdebug)hoserver.verproper.adda(msgdata, getName(),1);//文件记录元数据
		byte[] cleardata = receive(msgdata);//解码
		if(ServerWindow.isdebug)hoserver.verproper.adda(cleardata, getName(),0);//文件记录
		
		SocketMessage smg;
		 smg =	SocketMessage.obtain().parseData(cleardata);
		this.hoserver.datachat.addElement(smg.getCode()+","+Encdoutils.bytesToHexString(cleardata));//列表添加
		if(px != null)px.send(cleardata);//代理发送
		//读配置项文件中的发送回去
		if(hoserver.verproper.getcodelist().contains(Integer.valueOf(smg.getCode()))) {
			String resp[] = hoserver.verproper.getPerptitem(Integer.valueOf(smg.getCode())).split(",");
			for (String string : resp) {
				this.sendMessageData(string);
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
			socket.write(ByteBuffer.wrap(realdata));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void stopExcagent() {
		this.flag = false;
		delete_client();
		buf.clear();
		if(socket != null)
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}

}