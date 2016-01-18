package sogou.mobile.base.sogou_volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by liuyu on 16/1/5.
 */
public class SogouPoolingByteArrayOutputStream extends ByteArrayOutputStream {

    private static final int DEFAULT_SIZE = 256;
    private final SogouByteArrayPool mPool;

    public SogouPoolingByteArrayOutputStream(SogouByteArrayPool pool){
        mPool = pool;
    }

    public SogouPoolingByteArrayOutputStream(SogouByteArrayPool pool, int size){
        mPool = pool;
        buf = mPool.getBuffer(Math.max(size, DEFAULT_SIZE));
    }

    @Override
    public void close() throws IOException {
        mPool.returnBuffer(buf);
        buf = null;
        super.close();
    }

    private void expand(int i){
        if(count + i <=buf.length){
            return;
        }
        byte[] newbuf = mPool.getBuffer((count+i)*2);
        System.arraycopy(buf, 0, newbuf, 0, count);
        mPool.returnBuffer(buf);
        buf = newbuf;
    }

    @Override
    public synchronized void write(byte[] buffer, int offset, int len) {
        expand(len);
        super.write(buffer, offset, len);
    }

    @Override
    public synchronized void write(int oneByte) {
        equals(1);
        super.write(oneByte);
    }

    @Override
    protected void finalize() throws Throwable {
        mPool.returnBuffer(buf);
    }
}
