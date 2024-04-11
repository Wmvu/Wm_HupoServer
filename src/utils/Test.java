package utils;

//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.ByteBuffer;


public class Test {

	public static void main(String[] args) {
//		MessageDecode a = new MessageDecode();
//		byte[] w = {(byte) 0xd1,(byte) 0x61,(byte) 0x00,(byte) 0x07,(byte) 0xd1,(byte) 0xd1,(byte) 0xd1};
//		InputStream s = new ByteArrayInputStream(w);
//		System.out.println(MessageDecode.doDecode(s,new UserKey()));
//		System.out.println(Encdoutils.bytesToHexString(MessageDecodeUtil.encode(Encdoutils.hexStringToBytes("7FFE00090066010801"), MessageDecodeUtil.getEncodeKey())));
//		System.out.println(a.doDecode(Encdoutils.hexStringToBytes("74 C9 00 4D 99 BD E7 39 36 A1 9A 54 64 FF 1D 39 3F A1 BD D3 A8 19 32 B3 75 5B E7 F5 D7 05 41 26 66 90 B2 84 FA E7 10 EE 32 63 67 9D 39 AC 88 CE A9 B5 9F D2 89 7E 7F 43 C2 86 B2 A0 D2 57 20 C2 AA 5A E7 D9 EB 8F EC E3 E6 57 EF FF E7 ".replace(" ", ""))));
//		System.out.println(a.doDecode(Encdoutils.hexStringToBytes("A4A3001420178DAC2879A48264EB47A9C9022A2A".replace(" ", ""))));
//		System.out.println(a.doDecode(Encdoutils.hexStringToBytes("8F830014F31EAB9E7B063B41E7B734446EA63EBB")));
//		System.out.println(a.doDecode(Encdoutils.hexStringToBytes("71590007BC6CF41C5100070B2FEECFB100071992E91AEE00074CBED128CD0007F3D9C55F490007B6DA9A")));
//		System.out.println(a.doDecode(Encdoutils.hexStringToBytes("D973000990A0393767")));
//		ByteBuffer b = ByteBuffer.allocate(10);
//		s.read(b.array());
//		b.get();
//		System.out.println(Encdoutils.bytesToHexString(b.array()));
//		
		System.out.println("0a397b2262625f7474735f75726c223a22687474703a2f2f3139322e3136382e3133372e313a383038302f7474733f766f6963653d6875706f227d".replaceAll(" ", "").length());
	}

}
