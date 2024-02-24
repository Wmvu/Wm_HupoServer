package utils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.wm.start.Excagent;

public class VerProper {
	HashMap<Integer,String>codelist = null;
	HashMap<String,Integer> nodelist = null;
	public void initilise() {
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
