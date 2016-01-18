package sogou.mobile.base.sogou_volley;

import sogou.mobile.explorer.util.LogUtil;

/**
 * Created by liuyu on 16/1/4.
 */
public class SogouDispatcherThread extends Thread{

    protected volatile boolean mQuit = false;

    public void quit(){
        mQuit = true;
        interrupt();
    }

    public void startReally(){
        mQuit = false;
        start();
    }
}
