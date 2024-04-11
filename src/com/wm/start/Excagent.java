package com.wm.start;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Vector;

import com.gowild.dao.SocketMessage;
import com.gowild.dao.UserKey;

import utils.Encdoutils;
import utils.MessageDecode;
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
	ByteBuffer buf;
	Thread heart;
	int t =0;
	public Excagent (ServerWindow hoserver2,Socket socket) { 
	  try {
		this.hoserver= hoserver2;
		this.socket = socket;
		ou = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
		userkey = new UserKey();
		hoserver.verproper.ttime(this);
		buf = ByteBuffer.allocate(8192);
		
		heart =new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					if(t >= 60) stopExcagent();else t++;
					System.out.println(t);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		});
		heart.start();
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
				//接收
				t=0;
				byte[] revc = new byte [1024];
				int i = in.read(revc);
				buf.put(Arrays.copyOf(revc, i));
				buf.flip();
//				in.mark(1024);
//				buf.put((byte) 1);
//				byte[] msg = new byte[4];
//				in.read(msg);
				while (buf.limit()-buf.position()>6)on_pack();
				buf.compact();
			
			} catch (Exception e) {
				e.printStackTrace();
				try {
					flag = false;
					ou.close();
					in.close();
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				delete_client();
			}
		}
	}
	private void on_pack() throws IOException {
		int len = MessageDecode.doDecode(buf, this.userkey);
		if(len == 0 )return;
//		buf.get(msg);
//		int len = MessageDecode.doDecode(msg, this.userkey);//==
//		in.reset();
		byte[] msgdata = new byte[len];
//		byte[] lens = new byte[len-4];
//		in.read(lens);
		
//		System.arraycopy(msg, 0, msgdata, 0, 4);
//		System.arraycopy(lens, 0, msgdata, 4, len-4);
//		System.out.println(Encdoutils.bytesToHexString(msgdata));
		
//		buf.mark();
//		int len = MessageDecode.doDecode(buf, this.userkey);

//		byte[] msgdata = new byte[len];
		buf.get(msgdata);
		
		hoserver.verproper.adda(msgdata, getName(),1);//文件记录元数据
		byte[] cleardata = receive(msgdata);//解码
		hoserver.verproper.adda(cleardata, getName(),0);//文件记录
		
		SocketMessage smg;
		 smg =	SocketMessage.obtain().parseData(cleardata);
		this.hoserver.datachat.addElement(smg.getCode()+","+Encdoutils.bytesToHexString(cleardata));//列表添加
		if(px != null)px.send(cleardata);//代理发送
		//读配置项文件中的发送回去
		if(hoserver.verproper.getcodelist().contains(Integer.valueOf(smg.getCode()))) {
			String resp[] = hoserver.verproper.getPerptitem(Integer.valueOf(smg.getCode())).split(",");
			for (String string : resp) {
				System.out.println(string);
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
		
//		try {
//			if (!MessageDecode.doDecode(msgdata, this.userkey)){
//				return new byte[1];
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
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