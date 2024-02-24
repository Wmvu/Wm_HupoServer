package com.wm.start;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import utils.MessageDecodeUtil;

public class ExcAl extends Thread {
		ServerWindow hoserver;
		ServerSocket serversocket;
		Socket socket;
		boolean flag = true;

		public ExcAl(ServerWindow serverWindow){
			this.hoserver = serverWindow;
			this.serversocket = serverWindow.serversocket;
		
		}
		public void run () {
			while (flag) {
				try {
					socket = serversocket.accept();
					System.out.println("客户机连接");
					MessageDecodeUtil.resetKeys();
					Excagent et =new Excagent(hoserver, socket);
					et.start();
					hoserver.onlineList.add(et);
					hoserver.refreshlist();
				} catch (Exception e) {
					flag =false;
					try {
						if(socket != null)socket.close();
						socket = null;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
			}
		}
		
	}
//		@SuppressWarnings("resource")
		public void stopServer() {
			flag =false;
		}
}
