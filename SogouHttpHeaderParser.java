package sogou.mobile.base.sogou_volley;

import org.apache.http.protocol.HTTP;

import java.util.Map;

/**
 * Created by liuyu on 16/1/5.
 */
public class SogouHttpHeaderParser {

    //TODO
    public static Cache.Entry parseCacheHeader(SogouFrameWorkResponse response){

        return null;
    }

    //TODO
    public static long parseDataAsEpoch(String dataStr){

        return -1;
    }

    public static String parseCharset(Map<String, String> header){
        String contentType = header.get(HTTP.CONTENT_TYPE);
        if(contentType != null){
            String[] params = contentType.split(";");
            for(int i=0; i< params.length; i++){
                String[] pair = params[i].trim().split("=");
                if(pair.length == 2){
                    if(pair[0].equals("charset")){
                        return pair[1];
                    }
                }
            }
        }
        return HTTP.DEFAULT_CONTENT_CHARSET;
    }
}
