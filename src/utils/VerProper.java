package utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.wm.start.Excagent;

public class VerProper {
	HashMap<Integer,String>codelist = null;
	HashMap<String,Integer> nodelist = null;
	String po ="";
	int pt;
	boolean isproxy;
	public boolean isIsproxy() {
		return this.isproxy;
	}
	public void setIsproxy(boolean isproxy) {
		this.isproxy = isproxy;
	}
	public String getPo() {
		return po;
	}
	public int getPt() {
		return pt;
	}
	public void initilise() {
		setIsproxy(false);
		String path = System.getProperty("user.dir");
		Properties prt = new Properties();
		BufferedReader inStream;
		codelist = new HashMap<Integer,String>();
		nodelist = new HashMap<String,Integer>();
		try {
			inStream = new BufferedReader(new FileReader(path+"\\response.ini"));
			prt.load(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Set<Object> list = prt.keySet();
		List<Object>arlist = new ArrayList<Object>(list);
		for (Object object : arlist) {
			String lin = ((String) object).replaceAll(" ", "");
			if(lin.startsWith("T")) {
				String num = lin.substring(1, lin.length());
				Integer time = Integer.valueOf(num);
				nodelist.put(Encdoutils.doenc(prt.getProperty(lin)),time);
			}else if(lin.startsWith("SP")) {
				String num = lin.substring(2, lin.length());
				Integer port = Integer.valueOf(Encdoutils.doenc(prt.getProperty(lin)));
				pt = port;
				po = num;
				setIsproxy(true);
			}else {
				codelist.put(Integer.valueOf(lin),Encdoutils.doenc(prt.getProperty(String.valueOf(lin))));
			}
			
		}
	}
public void ttime(Excagent exa) {
	new Thread(new Runnable() {
		@Override
		public void run() {
			int i=1;
		while(exa.flag) {
			try {
				for (String string : nodelist.keySet()) {
					if(i % nodelist.get(string) == 0)exa.sendMessageData(string);
				}
				i++;
				Thread.sleep(1000);
			} catch (Exception e) {;
				e.printStackTrace();
			}
		}
		}
	}).start();
}
public String getPerptitem(Integer code) {
	return this.codelist.get(code);
}
public List<Object> getcodelist() {
	return new ArrayList<Object>(this.codelist.keySet());
}
}
