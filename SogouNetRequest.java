package sogou.mobile.base.sogou_volley;



import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

import sogou.mobile.framework.dir.DirType;

/**
 * Created by liuyu on 16/1/4.
 */
public abstract class SogouNetRequest<T> extends SogouBaseRequest{

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    public static final long FILE_EXPIRED_TIME = 3600000*24*3;
    private final String mUrl;

    private DirType mDirType;


    public interface Method{
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    private int mMethod;
    private int mTimeout = 10000;

    public boolean mIntermediate = false;
    public boolean mShouldParse = true;

    private Cache.Entry mCacheEntry = null;

    public SogouNetRequest(String url, int method, int requestType, SogouResponse.OnErrorListener errorListener){
        super(requestType, errorListener);
        mUrl = url;
        mMethod = method;

    }

    public SogouNetRequest(String url, int method, SogouResponse.OnErrorListener errorListener){
        this(url, method, RequestType.NET, errorListener);
    }

    public String getUrl(){
        return mUrl;
    }

    @Override
    public String getCacheKey() {
        return getUrl();
    }

    public SogouBaseRequest<?> setCacheEntry(Cache.Entry entry){
        mCacheEntry = entry;
        return this;
    }

    public Cache.Entry getCacheEntry(){
        return mCacheEntry;
    }

    public void setIntermediate(boolean intermediate){
        mIntermediate = intermediate;
    }

    public boolean isIntermediate(){
        return mIntermediate;
    }

    //TODO
    public Map<String, String> getHeaders(){
//        return getParams();
        return Collections.emptyMap();
    }

    public int getMethod(){
        return mMethod;
    }

    public void setMethod(int method){
        this.mMethod = method;
    }

    public void setTimeout(int time){
        this.mTimeout = time;
    }

    public int getTimeout(){
        return mTimeout;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getPostParamsEncoding(){
        return getParamsEncoding();
    }

    //这个是普通的未经转换的body
    protected Map<String, String> getParams(){
        return null;
    }

    //这个是urlencode编码的body
    public byte[] getBody(){
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
}
