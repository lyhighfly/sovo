package sogou.mobile.base.sogou_volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import sogou.mobile.framework.util.FileUtil;

/**
 * Created by liuyu on 16/1/7.
 */
public class SogouCacheImpl2 implements Cache{


    private static final int DEFAULT_POOL_SIZE = 4096;
    private SogouByteArrayPool mPool;

    //业务逻辑不太一样，原map是用在将所有的缓存图片，简略信息全部存在map中，这样在map中可以存取数据，并以此来判断缓存是否存在，
    //但是，在此场景中，并不适用；
//    private final Map<String, byte[]> mEntries = new LinkedHashMap<String, byte[]>(16, .75f, true);
//    private long mTotalSize = 0;
//    private final int mMaxCacheSizeInBytes;

    public SogouCacheImpl2(){

        this(new SogouByteArrayPool(DEFAULT_POOL_SIZE));
    }

    public SogouCacheImpl2(SogouByteArrayPool pool){

        mPool = pool;
    }

    @Override
    public Entry getEntry(SogouNetRequest key) {

        String fileName = getFileForKey(key.getCacheKey());
        Entry entry = new Entry();
        try {
            File cacheFile = new File(fileName);
            byte[] content = readToBytes(cacheFile);
            entry.data = content;
            entry.isExpired = (System.currentTimeMillis() - cacheFile.lastModified()) > key.FILE_EXPIRED_TIME ? true:false;
            return entry;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void putEntry(SogouNetRequest key, Entry entry) {
        String fileName = getFileForKey(key.getCacheKey());
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(new File(fileName));
            fos.write(entry.data, 0, entry.data.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos != null){
                try{
                    fos.flush();
                    fos.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void removeEntry(Entry entry) {
        //DO NOTHING
    }

    @Override
    public void clear() {

    }

    //TODO
    @Override
    public void invalidate(SogouNetRequest key, boolean fullExpire) {

        String fileName = getFileForKey(key.getUrl());
        new File(fileName).delete();
    }

    @Override
    public void initialize() {
        //DO NOTHING
    }

    private byte[] readToBytes(String cacheFileName)throws IOException{
        return readToBytes(new File(cacheFileName));
    }

    private byte[] readToBytes(File cacheFile)throws IOException{
        SogouPoolingByteArrayOutputStream byteArrayOutputStream =
                new SogouPoolingByteArrayOutputStream(mPool, (int) cacheFile.length());
        byte[] buffer = null;
        InputStream inputStream = null;
        try{
            inputStream = new FileInputStream(cacheFile);
            if(inputStream == null){
                throw new IOException("cache-not-read-file");
            }
            buffer = mPool.getBuffer(1024);
            int count;
            while ((count = inputStream.read(buffer)) != -1){
                byteArrayOutputStream.write(buffer, 0, count);
            }
            return byteArrayOutputStream.toByteArray();
        }finally {
            try{
                if(inputStream != null)
                    inputStream.close();
            }catch (IOException e){

            }
            mPool.returnBuffer(buffer);
            byteArrayOutputStream.close();
        }
    }

    //TODO
    private String getFileForKey(String key){
        String fileName = FileUtil.buildContentCacheFileName(key);
        return fileName;
    }
}
