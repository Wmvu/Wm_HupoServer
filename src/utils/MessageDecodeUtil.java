package utils;


import java.util.Arrays;

/* loaded from: classes.dex */
public final class MessageDecodeUtil {
//    private static final int STEP_SEED = 1;
    private static final int[] DEFAULT_KEY = {174, 191, 86, 120, 171, 205, 239, 241};
    private static int[] encodeKeys = null;
    private static int[] decodeKeys = null;

    private MessageDecodeUtil() {
    }

    public static void resetKeys() {
        encodeKeys = null;
        decodeKeys = null;
    }

    public static byte[] encode(byte[] plainText, int[] encryptKey) {
        int length = plainText.length;
        int lastCipherByte = (byte) ((plainText[0] ^ encryptKey[0]) & 255);
        plainText[0] = (byte) lastCipherByte;
        encryptKey[1] = (encryptKey[1] ^ lastCipherByte) & 255;
        int lastCipherByte2 = (((plainText[1] ^ encryptKey[1]) & 255) + lastCipherByte) & 255;
        plainText[1] = (byte) lastCipherByte2;
        int keyLen = encryptKey.length;
        int i = 4;
        int j = 0;
        while (i < length) {
            if (j == keyLen) {
                j = 0;
            }
            encryptKey[j] = ((encryptKey[j] + lastCipherByte2) ^ i) & 255;
            lastCipherByte2 = (((plainText[i] ^ encryptKey[j]) & 255) + lastCipherByte2) & 255;
            plainText[i] = (byte) lastCipherByte2;
            i++;
            j++;
        }
        return plainText;
    }

    public static byte[] decode(byte[] data, int[] decryptKey) {
        if (data.length != 0) {
            int length = data.length;
            int lastCipherByte = data[0] & 255;
            data[0] = (byte) (data[0] ^ decryptKey[0]);
            int key = decryptKey[1] ^ lastCipherByte;
            int plainText = (((data[1] & 255) - lastCipherByte) ^ key) & 255;
            int lastCipherByte2 = data[1] & 255;
            data[1] = (byte) plainText;
            decryptKey[1] = key & 255;
            int keyLen = decryptKey.length;
            int i = 4;
            int j = 0;
            while (i < length) {
                if (j == keyLen) {
                    j = 0;
                }
                int key2 = (decryptKey[j] + lastCipherByte2) ^ i;
                int plainText2 = (((data[i] & 255) - lastCipherByte2) ^ key2) & 255;
                lastCipherByte2 = data[i] & 255;
                data[i] = (byte) plainText2;
                decryptKey[j] = key2 & 255;
                i++;
                j++;
            }
        }
        return data;
    }

    public static int[] copyDefaultKey() {
        return Arrays.copyOf(DEFAULT_KEY, DEFAULT_KEY.length);
    }

    public static int[] getEncodeKey() {
        if (encodeKeys == null) {
            encodeKeys = copyDefaultKey();
        }
        return encodeKeys;
    }

    public static int[] getDecodeKey() {
        if (decodeKeys == null) {
            decodeKeys = copyDefaultKey();
        }
        return decodeKeys;
    }
}