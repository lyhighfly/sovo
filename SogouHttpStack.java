package sogou.mobile.base.sogou_volley;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liuyu on 16/1/6.
 */
public interface SogouHttpStack {

    public HttpResponse performRequest(SogouNetRequest<?> request, Map<String, String> additionHeader)
        throws IOException;
}
