package com.gowild.dao;

import java.nio.ByteBuffer;
import java.util.Arrays;


interface OnSendMessageErrorListener {
    void sendMessageError(SocketMessage socketMessage);
}
interface TimeoutListener {
    public static final String TAG = TimeoutListener.class.getSimpleName();

    void timeout(short s);
}
public final class SocketMessage {
//    private static final int FLAG_IN_USE = 1;
    public static final short HEADER = 32766;
    public static final short HEAD_SIZE = 7;
//    private static final int MAX_POOL_SIZE = 20;
    public static final byte TYPE = 1;
    private static SocketMessage sPool;
    public boolean bSendSync;
    private byte[] body;
    short code;
    private int flags;
    public OnSendMessageErrorListener mErrorListenter;
    public TimeoutListener mTimeOutListenter;
    SocketMessage next;
    public short returnCode;
    public short tag;
    private static final Object sPoolsync = new Object();
    private static int sPoolSize = 0;

    public SocketMessage(short code) {
        this.flags = 0;
        this.code = (short) -1;
        this.returnCode = (short) -1;
        this.tag = (short) 0;
        this.body = new byte[0];
        this.bSendSync = false;
        this.code = code;
    }

    public SocketMessage() {
        this.flags = 0;
        this.code = (short) -1;
        this.returnCode = (short) -1;
        this.tag = (short) 0;
        this.body = new byte[0];
        this.bSendSync = false;
    }

    public void setCode(short code) {
        this.code = code;
    }

    public short getCode() {
        return this.code;
    }

    public SocketMessage setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public byte[] getBody() {
        return this.body;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInUse() {
        return (this.flags & 1) == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void markInUse() {
        this.flags |= 1;
    }

    public static SocketMessage obtain() {
        synchronized (sPoolsync) {
            if (sPool != null) {
                SocketMessage msg = sPool;
                sPool = msg.next;
                msg.next = null;
                msg.flags = 0;
                msg.code = (short) -1;
                msg.returnCode = (short) -1;
                msg.mErrorListenter = null;
                msg.mTimeOutListenter = null;
                sPoolSize--;
                return msg;
            }
            return new SocketMessage();
        }
    }

    public static SocketMessage obtain(short code) {
        synchronized (sPoolsync) {
            if (sPool != null) {
                SocketMessage msg = sPool;
                sPool = msg.next;
                msg.next = null;
                msg.flags = 0;
                msg.code = code;
                msg.returnCode = (short) -1;
                msg.mErrorListenter = null;
                msg.mTimeOutListenter = null;
                sPoolSize--;
                return msg;
            }
            return new SocketMessage(code);
        }
    }

    public void recycle() {
        this.flags = 1;
        this.code = (short) -1;
        this.returnCode = (short) -1;
        this.body = null;
        synchronized (sPoolsync) {
            if (sPoolSize < 20) {
                this.next = sPool;
                sPool = this;
                sPoolSize++;
                this.mErrorListenter = null;
                this.mTimeOutListenter = null;
            }
        }
    }

    public void copyMessage(SocketMessage msg) {
        this.flags = msg.flags;
        this.code = msg.code;
        this.returnCode = msg.returnCode;
        this.body = msg.body == null ? null : (byte[]) msg.body.clone();
    }

    public SocketMessage parseData(byte[] data) {
        if (data.length < 7) {
            return new SocketMessage();
        }
        SocketMessage msg = new SocketMessage();
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        byteBuffer.position(4);
        msg.code = byteBuffer.getShort();
        byteBuffer.get();
        if (data.length - 7 > 0) {
            msg.body = new byte[data.length - 7];
            byteBuffer.get(msg.body);
            return msg;
        }
        return msg;
    }

    public ByteBuffer toByteBuffer() {
        short len = this.body == null ? (short) 7 : (short) (this.body.length + 7);
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.putShort(HEADER);
        byteBuffer.putShort(len);
        byteBuffer.putShort(this.code);
        byteBuffer.put((byte) 1);
        if (this.body != null) {
            byteBuffer.put(this.body);
        }
        return byteBuffer;
    }

    public byte[] toBytes() {
        return toByteBuffer().array();
    }

    public SocketMessage setErrorListenter(OnSendMessageErrorListener errorListenter) {
        this.mErrorListenter = errorListenter;
        return this;
    }

    public SocketMessage setReturnCode(short returnCode) {
        this.returnCode = returnCode;
        return this;
    }

    public SocketMessage setTimeOutListenter(TimeoutListener timeOutListenter) {
        this.mTimeOutListenter = timeOutListenter;
        return this;
    }

    public SocketMessage sendSync(boolean sync) {
        this.bSendSync = sync;
        return this;
    }

    public String toString() {
        return "SocketMessage{next=" + this.next + ", flags=" + this.flags + ", code=" + ((int) this.code) + ", returnCode=" + ((int) this.returnCode) + ", body=" + Arrays.toString(this.body) + ", mErrorListenter=" + this.mErrorListenter + ", mTimeOutListenter=" + this.mTimeOutListenter + '}';
    }
}