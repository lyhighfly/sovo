package sogou.mobile.base.sogou_volley;

import android.net.http.HttpsConnection;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.http.Header;
import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by liuyu on 16/1/6.
 */
public class SogouHttpClientStack implements SogouHttpStack{

    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    public UrlRewriter mRewriter;
    public SSLSocketFactory mSSLSocketFactory;

    public interface UrlRewriter{
        public String rewriteUrl(String originalUrl);
    }

    public SogouHttpClientStack(){

        this(null);
    }
    public SogouHttpClientStack(UrlRewriter rewriter){

        this(rewriter, null);
    }
    public SogouHttpClientStack(UrlRewriter rewriter, SSLSocketFactory sslSocketFactory){

        this.mRewriter = rewriter;
        this.mSSLSocketFactory = sslSocketFactory;
    }

    @Override
    public HttpResponse performRequest(SogouNetRequest<?> request, Map<String, String> additionHeader) throws IOException {
        String url = request.getUrl();
        HashMap<String, String> map = new HashMap<String, String>();
        map.putAll(request.getHeaders());
        map.putAll(additionHeader);
        if(mRewriter != null){
            String rewritten = mRewriter.rewriteUrl(url);
            if(rewritten == null){
                throw new IOException("URL blocked by rewriter:"+url);
            }
            url = rewritten;
        }
        URL parseUrl = new URL(url);
        HttpURLConnection connection = openConnection(parseUrl, request);
        for(String headerName : map.keySet()){
            connection.addRequestProperty(headerName, map.get(headerName));
        }

        /***************************************/
        connection.setRequestProperty("Accept-Encoding","gzip");
        /**************************************/

        setConnectionParametersForRequest(connection, request);
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = connection.getResponseCode();
        if(responseCode == -1){
            throw new IOException("can`t receive response code from connection");
        }
        StatusLine responseStatus = new BasicStatusLine(protocolVersion,
                connection.getResponseCode(), connection.getResponseMessage());
        BasicHttpResponse response = new BasicHttpResponse(responseStatus);
        response.setEntity(entityFromConnection(connection));
        for(Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()){
            if(header.getKey() != null){
                Header header1 = new BasicHeader(header.getKey(), header.getValue().get(0));
                response.addHeader(header1);
            }
        }
        return response;
    }

    //TODO
    private HttpURLConnection openConnection(URL url, SogouNetRequest<?> request)throws IOException{
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        int timeoutMS = request.getTimeout();
        connection.setConnectTimeout(timeoutMS);
        connection.setReadTimeout(timeoutMS);
        connection.setUseCaches(false);
        connection.setDoInput(true);

        if("https".equals(url.getProtocol()) && mSSLSocketFactory != null){
            ((HttpsURLConnection)connection).setSSLSocketFactory(mSSLSocketFactory);
        }
        return connection;
    }

    //TODO HttpEntity中包含了基本信息，最关键的是inputStream
    private static HttpEntity entityFromConnection(HttpURLConnection connection){

        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream inputStream;
        try{
            inputStream = connection.getInputStream();
        }catch (IOException e){
            inputStream = connection.getErrorStream();
        }
        entity.setContent(inputStream);
        entity.setContentLength(connection.getContentLength());
        entity.setContentEncoding(connection.getContentEncoding());
        entity.setContentType(connection.getContentType());
        return  entity;
    }

    //TODO
    void setConnectionParametersForRequest(HttpURLConnection connection,  SogouNetRequest<?> request)throws IOException{
        switch (request.getMethod()){
            case SogouNetRequest.Method.DEPRECATED_GET_OR_POST:
//                byte[] postBody = request.getBody();
                byte[] postBody = buildBody(request);
                if(postBody != null){
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
//                    connection.addRequestProperty(HEADER_CONTENT_TYPE, request.getPostParamsEncoding());
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(postBody);
                    out.flush();
                    out.close();
                }
                break;
            case SogouNetRequest.Method.GET:
                connection.setRequestMethod("GET");
                break;
            case SogouNetRequest.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case SogouNetRequest.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case SogouNetRequest.Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case SogouNetRequest.Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case SogouNetRequest.Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case SogouNetRequest.Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case SogouNetRequest.Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type");
        }
    }

    private byte[] buildBody(SogouNetRequest request){
        JSONObject object = new JSONObject(request.getParams());
        return object.toString().getBytes();
    }
    private static void addBodyIfExists(HttpURLConnection connection, SogouNetRequest<?> request)
            throws IOException{
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(HEADER_CONTENT_TYPE, request.getPostParamsEncoding());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }
}
