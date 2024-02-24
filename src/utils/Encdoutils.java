package utils;

public class Encdoutils {

    public static byte[] hexStringToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }
    public static String doenc(String e) {
    	String a = e.replaceAll(" ", "");
		return a;
    }
//    public static byte[] doenc2b(String e) {
//    	String a = e.replaceAll(" ", "");
//		byte c[] = MessageDecodeUtil.decode(hexStringToBytes(a),MessageDecodeUtil.getDecodeKey());
//		return c;
//    }
}
