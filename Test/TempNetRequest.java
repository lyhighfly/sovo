package sogou.mobile.base.sogou_volley.Test;

import sogou.mobile.base.sogou_volley.SogouFrameWorkResponse;
import sogou.mobile.base.sogou_volley.SogouNetRequest;
import sogou.mobile.base.sogou_volley.SogouResponse;
import sogou.mobile.explorer.util.LogUtil;

/**
 * Created by liuyu on 16/1/12.
 */
public class TempNetRequest extends SogouNetRequest {
    private SogouResponse.Listener okListener;
    public TempNetRequest(String url, int method, SogouResponse.OnErrorListener listener,SogouResponse.Listener okListener){
        super(url, method, listener);
        this.okListener = okListener;
    }

    @Override
    protected SogouResponse parseResponse(SogouFrameWorkResponse response) {
        String data = new String(response.toString());
        return SogouResponse.success(data, null);
    }

    @Override
    protected void onRequestNext(SogouResponse response) {
        LogUtil.e("hello world from onRequestNext  net：："+Thread.currentThread().getId());
        super.onRequestNext(response);
    }

    @Override
    protected void deliverResponse(Object response) {
        if(okListener != null){
            okListener.onResponse(response);
        }
    }
}
