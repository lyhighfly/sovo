package sogou.mobile.base.sogou_volley.Test;

import sogou.mobile.base.sogou_volley.RequestQueue;
import sogou.mobile.base.sogou_volley.SoVolley;
import sogou.mobile.base.sogou_volley.SogouBaseError;
import sogou.mobile.base.sogou_volley.SogouLocalRequest;
import sogou.mobile.base.sogou_volley.SogouNetRequest;
import sogou.mobile.base.sogou_volley.SogouFrameWorkResponse;
import sogou.mobile.base.sogou_volley.SogouResponse;
import sogou.mobile.explorer.BrowserApp;
import sogou.mobile.explorer.util.LogUtil;

/**
 * Created by liuyu on 16/1/6.
 */
public class SoVolleyTest {

    TempLocalRequest tempLocalRequest = new TempLocalRequest(new SogouResponse.OnErrorListener() {
        @Override
        public void onErrorListener(SogouBaseError error) {
            LogUtil.e("oh errorrrr");
        }
    }, new SogouResponse.Listener(){
        @Override
        public void onResponse(Object response) {
            LogUtil.e("LocalWork onResponse:::"+Thread.currentThread().getId());
        }
    });

    Temp1NetRequest temp1NetRequest = new Temp1NetRequest("http://www.baidu.com/", SogouNetRequest.Method.GET, new SogouResponse.OnErrorListener() {
        @Override
        public void onErrorListener(SogouBaseError error) {
            LogUtil.e("oh errorrrr");
        }
    }, new SogouResponse.Listener() {
        @Override
        public void onResponse(Object response) {
            LogUtil.e("NetWork onResponse:::"+Thread.currentThread().getId());
        }
    });

    public void test() {
        RequestQueue requestQueue = BrowserApp.getApplication().mRequestQueue;
        requestQueue.add(tempLocalRequest);
        requestQueue.add(temp1NetRequest);
    }
}

class TempLocalRequest extends SogouLocalRequest<String>{

    private SogouResponse.Listener mOkListener;

    public TempLocalRequest(SogouResponse.OnErrorListener errorlistener, SogouResponse.Listener listener) {
        super(errorlistener);
        this.mOkListener = listener;
    }

    @Override
    protected void onRequestNext(SogouResponse response) {
        LogUtil.e("LocalWork OnRequestNext:::"+Thread.currentThread().getId());
        super.onRequestNext(response);
    }

    @Override
    protected SogouResponse<String> requestBody() {

        return SogouResponse.success("hello world from SoVolley", null);
    }

    @Override
    protected void deliverResponse(Object response) {
        mOkListener.onResponse(response);
    }
}

class Temp1NetRequest extends SogouNetRequest{

    private SogouResponse.Listener okListener;
    public Temp1NetRequest(String url, int method, SogouResponse.OnErrorListener listener,SogouResponse.Listener okListener){
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
        LogUtil.e("NetWork OnRequestNext:::"+Thread.currentThread().getId());
        super.onRequestNext(response);
    }

    @Override
    protected void deliverResponse(Object response) {
        if(okListener != null){
            okListener.onResponse(response);
        }
    }
}
