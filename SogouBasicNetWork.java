package sogou.mobile.base.sogou_volley;

import android.os.SystemClock;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import sogou.mobile.explorer.util.LogUtil;

/**
 * Created by liuyu on 16/1/5.
 */
public class SogouBasicNetWork implements SogouNetWork{

    private static int DEFAULT_POOL_SIZE = 4096;

    private SogouByteArrayPool mPool;

    private SogouHttpStack mStack;

    public SogouBasicNetWork(SogouHttpStack stack){
        this(stack, new SogouByteArrayPool(DEFAULT_POOL_SIZE));
    }

    public SogouBasicNetWork(SogouHttpStack stack, SogouByteArrayPool pool){
        mStack = stack;
        mPool = pool;
    }

    @Override
    public SogouFrameWorkResponse performRequest(SogouNetRequest<?> request) throws SogouBaseError {
        long requestStart = SystemClock.elapsedRealtime();

            HttpResponse response = null;
            byte[] responseContents = null;
            Map<String, String> responseHeader = Collections.emptyMap();
            try{
                Map<String, String> headers = new HashMap<String, String>();
                addCacheHeaders(headers, request.getCacheEntry());
                response = mStack.performRequest(request, headers);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                responseHeader = convertHeaders(response.getAllHeaders());

                if(statusCode == HttpStatus.SC_NOT_MODIFIED){

                    Cache.Entry entry = request.getCacheEntry();
                    if(entry == null){
                        return  new SogouFrameWorkResponse(HttpStatus.SC_NOT_MODIFIED, null,
                                responseHeader, true, SystemClock.elapsedRealtime()-requestStart);
                    }
                    entry.responseHeader.putAll(responseHeader);
                    return new SogouFrameWorkResponse(HttpStatus.SC_NOT_MODIFIED, entry.data,
                            entry.responseHeader, true,
                            SystemClock.elapsedRealtime() - requestStart);
                }

                if(statusCode < 200 || statusCode > 299) {
                    throw  new IOException();
                }

                //是底层解析基本数据，还是将stream交给业务层
                if(request.mShouldParse) {
                    if (response.getEntity() != null) {
                        responseContents = entityToBytes(response.getEntity());
                    } else {
                        responseContents = new byte[0];
                    }
                    return new SogouFrameWorkResponse(statusCode, responseContents,
                            responseHeader, false, SystemClock.elapsedRealtime()-requestStart);
                }else{
                    return new SogouFrameWorkResponse(statusCode, responseHeader, response.getEntity());
                }

            } catch (SocketTimeoutException e) {
                //TODO retry Policy
            } catch (ConnectTimeoutException e) {
                //TODO retry Policy
            } catch (MalformedURLException e) {
                //TODO retry Policy
            }catch (IOException e){
                //TODO retry Policy
                e.printStackTrace();
            }
        return null;
    }

    //TODO
    private void addCacheHeaders(Map<String, String> headers, Cache.Entry entry){

    }

    //TODO 在此阶段了上次可能获取的流，如果上层需要断点续传，是否可以将此步跳过？？？
    private byte[] entityToBytes(HttpEntity entity)throws IOException, SogouServerError{
        SogouPoolingByteArrayOutputStream byteArrayOutputStream =
                new SogouPoolingByteArrayOutputStream(mPool,(int)entity.getContentLength());
        byte[] buffer = null;
        try{
            InputStream inputStream = entity.getContent();
            if(inputStream == null){
                throw new SogouServerError();
            }
            buffer = mPool.getBuffer(1024);
            int count;
            while ((count = inputStream.read(buffer)) != -1){
                byteArrayOutputStream.write(buffer, 0, count);
            }
            return byteArrayOutputStream.toByteArray();
        }finally {
            try{
                entity.consumeContent();
            }catch (IOException e){

            }
            mPool.returnBuffer(buffer);
            byteArrayOutputStream.close();
        }
    }

    private static Map<String, String> convertHeaders(Header[] headers){

        Map<String, String> result = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        for(int i=0; i<headers.length; i++){
            result.put(headers[i].getName(), headers[i].getValue());
        }
        return result;
    }
}
