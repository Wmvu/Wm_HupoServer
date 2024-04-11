package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.wm.start.Excagent;
import com.wm.start.ServerWindow;

public class Proxy extends Thread {
	Socket sc = null;
	OutputStream out;
	InputStream ipt;
	Socket cs = null;
	Excagent exc;
	boolean flag = false;
	ServerWindow sw;
public Excagent getExc() {
		return exc;
	}
	public void setExc(Excagent exc) {
		this.exc = exc;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public Proxy(ServerWindow sw) {
		this.sw = sw;
	}
	@Override
public void run() {
	try {
		while (true) {
			if(flag) {
		byte[] msg = new byte[4];
		int n = sc.getInputStream().read(msg);
//		byte[] msgdata = Arrays.copyOf(msg, n);
//		byte[] cleardata = MessageDecodeUtil.decode(msgdata, MessageDecodeUtil.getDecodeKey());
		
		int len = MessageDecode.doDecode(msg, MessageDecodeUtil.getDecodeKey());
		if (n<6)continue;
//		buf.get(msg);
//		int len = MessageDecode.doDecode(msg, this.userkey);
		byte[] lens = new byte[len-4];
		byte[] cleardata = new byte[len];
		sc.getInputStream().read(lens);
		
		System.arraycopy(msg, 0, cleardata, 0, 4);
		System.arraycopy(lens, 0, cleardata, 4, len-4);
//		System.out.println(Encdoutils.bytesToHexString(msgdata));
		this.rend(cleardata);
			}
		}
		
	} catch (IOException e) {
		flag = false;
		try {
			sc.shutdownOutput();
			sc.shutdownInput();
			sc.close();
		} catch (IOException e1) {
		}
		
	}
}
	public void send(byte[] st) throws IOException{
		if (!flag) return;
		byte[] by = MessageDecodeUtil.encode(st, MessageDecodeUtil.getEncodeKey());
		out.write(by);
		out.flush();
	}
	public void rend(byte[] b){
		if (flag) this.exc.sendMessageData(b);;
	}
	public void reset(Excagent exc) throws IOException {
		if(sc!=null) {
			try {
				sc.shutdownOutput();
				sc.shutdownInput();
				sc.close();
			} catch (IOException e1) {System.out.println("1");};
		}
		String iphost =this.sw.verproper.getPo();
		Integer port = this.sw.verproper.getPt();
		System.out.println(iphost +"==>"+port);
		if(!sw.verproper.isIsproxy())return;
		setExc(exc);
		getExc().setPx(this);
		sc = new Socket(iphost,port);
		MessageDecodeUtil.resetKeys();
		out = sc.getOutputStream();
		ipt = sc.getInputStream();
		flag = true;
	}
}
