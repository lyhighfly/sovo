package sogou.mobile.base.sogou_volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Created by liuyu on 16/1/5.
 */
public class SogouFrameWorkResponse {

    public final int statusCode;
    public final byte[] data;
    public final Map<String, String> header;
    public final boolean notModified;
    public final long networkTimeMs;

    public HttpEntity mEntity;
    public boolean mNotBasicParse = false;

    public SogouFrameWorkResponse(int statusCode, byte[] data, Map<String, String> header,
                                  boolean notModified, long networkTimeMs){
        this.statusCode = statusCode;
        this.data = data;
        this.header = header;
        this.notModified = notModified;
        this.networkTimeMs = networkTimeMs;
    }
    public SogouFrameWorkResponse(int statusCode, byte[] data, Map<String, String> header,
                                  boolean notModified){
        this(statusCode, data, header, notModified, 0);
    }

    public SogouFrameWorkResponse(byte[] data, Map<String, String> header){
        this(HttpStatus.SC_OK, data, header, false, 0);
    }

    public SogouFrameWorkResponse(byte[] data){
        this(HttpStatus.SC_OK, data, Collections.<String, String>emptyMap(), false, 0);
    }

    public SogouFrameWorkResponse(int statusCode, Map<String, String> header, HttpEntity entity){
        this.statusCode = statusCode;
        this.header = header;
        this.mEntity = entity;
        this.data = null;
        this.notModified = false;
        this.networkTimeMs = 0;
        this.mNotBasicParse = true;
    }

}
