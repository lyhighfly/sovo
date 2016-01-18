package sogou.mobile.base.sogou_volley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuyu on 16/1/5.
 */
public class SogouByteArrayPool {

    private List<byte[]> mBuffersByLastUse = new LinkedList<byte[]>();
    private List<byte[]> mBuffersBySize = new ArrayList<byte[]>();

    private int mCurrentSize = 0;

    private final int mSizeLimit;

    public SogouByteArrayPool(int sizeLimit){
        mSizeLimit = sizeLimit;
    }

    protected static final Comparator<byte[]> BUF_COMPARATOR = new Comparator<byte[]>() {
        @Override
        public int compare(byte[] lhs, byte[] rhs) {
            return lhs.length -rhs.length;
        }
    };

    public synchronized byte[] getBuffer(int len){

        for(int i=0; i<mBuffersBySize.size(); i++){
            byte[] buf = mBuffersBySize.get(i);
            if(buf.length >= len){
                mCurrentSize -= buf.length;
                mBuffersBySize.remove(buf);
                mBuffersByLastUse.remove(buf);
                return buf;
            }
        }
        return new byte[len];
    }

    public synchronized void returnBuffer(byte[] buf){

        if(buf == null || buf.length > mSizeLimit){
            return;
        }
        mBuffersByLastUse.add(buf);
        int pos = Collections.binarySearch(mBuffersBySize, buf, BUF_COMPARATOR);
        if(pos < 0){
            pos = -pos - 1;
        }
        mBuffersBySize.add(pos, buf);
        mCurrentSize += buf.length;
        trim();
    }

    public synchronized void trim(){

        while(mCurrentSize > mSizeLimit){
            byte[] buf = mBuffersByLastUse.get(0);
            mBuffersBySize.remove(buf);
            mCurrentSize -= buf.length;
        }
    }
}
