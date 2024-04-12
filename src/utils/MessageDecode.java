package utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.gowild.dao.SocketMessage;
import com.gowild.dao.UserKey;

public class MessageDecode {
    public static int doDecode(byte[] msg,int[] ukey) throws IOException {
        int connectiona = msg.length;
        if (connectiona < 4) {
            return 0;
        }
        int[] decryptKey = ukey;
        int cipherByte1 = msg[0] & ((byte)-1);
        int key = decryptKey[0];
        int plainByte1 = (cipherByte1 ^ key) & 255;
        int cipherByte2 = msg[1] & ((byte)-1);
        int key2 = (decryptKey[1] ^ cipherByte1) & 255;
        int plainByte2 = ((cipherByte2 - cipherByte1) ^ key2) & 255;
        int header = (plainByte1 << 8) + plainByte2;
        int len = (int)((msg[2]<<8 | msg[3]) & 0xff);
        if (header != 32766) {
            return 0;
        }
        return len;
    }
    public SocketMessage doDecode(byte[] connection) throws IOException {
        int connectiona = connection.length;
//        LogUtils.getLog(MessageDecode.class).debug(connectiona + ":length");
        if (connectiona < 4) {
            return new SocketMessage();
        }
//        connection.markReadPosition();
        int[] decryptKey = MessageDecodeUtil.getDecodeKey();
        int cipherByte1 = connection[0] & ((byte)-1);
        int key = decryptKey[0];
        int plainByte1 = (cipherByte1 ^ key) & 255;
        int cipherByte2 = connection[1] & ((byte)-1);
        int key2 = (decryptKey[1] ^ cipherByte1) & 255;
        int plainByte2 = ((cipherByte2 - cipherByte1) ^ key2) & 255;
        int header = (plainByte1 << 8) + plainByte2;
        int len = (int)(connection[2]<<8 | connection[3]);
//        connection.resetToReadMark();
        if (header != 32766) {
            return new SocketMessage();
        }
        if (connectiona < len) {
            return new SocketMessage();
        }
//        System.out.println(String.format("%02X",header));
        byte[] datas = new byte[len];
        datas = Arrays.copyOf(connection,len);
        return SocketMessage.obtain().parseData(MessageDecodeUtil.decode(datas, MessageDecodeUtil.getDecodeKey()));
    }
	public static int doDecode(ByteBuffer buf, UserKey ukey) {
		int connectiona = buf.limit() - buf.position();
        if (connectiona < 4) {
            return 0;
        }
        buf.mark();
        int[] decryptKey = ukey.getDekey();
        int cipherByte1 = buf.get() & ((byte)-1);
        int key = decryptKey[0];
        int plainByte1 = (cipherByte1 ^ key) & 255;
        int cipherByte2 = buf.get() & ((byte)-1);
        int key2 = (decryptKey[1] ^ cipherByte1) & 255;
        int plainByte2 = ((cipherByte2 - cipherByte1) ^ key2) & 255;
        int header = (plainByte1 << 8) + plainByte2;
        int len = buf.getShort();
        buf.reset();
        if (header != 32766) {
        	buf.get();
            return doDecode(buf,ukey);
        }
        if (connectiona < len) {
            return 0;
        }
        return len;
	}
}