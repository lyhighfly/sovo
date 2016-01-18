package sogou.mobile.base.sogou_volley;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by liuyu on 16/1/4.
 */
public class SogouCacheDispatcher extends SogouDispatcherThread{

    private PriorityBlockingQueue<SogouNetRequest<?>> mCacheQueue;
    private PriorityBlockingQueue<SogouNetRequest<?>> mNetWorkQueue;
    private Cache mCache;
    private ResponseDelivery mDelivery;

    public SogouCacheDispatcher(PriorityBlockingQueue<SogouNetRequest<?>> cacheQ,
                                PriorityBlockingQueue<SogouNetRequest<?>> netQ,
                                Cache cache,  ResponseDelivery delivery){
        mCacheQueue = cacheQ;
        mNetWorkQueue = netQ;
        mCache = cache;
        mDelivery = delivery;
    }

    @Override
    public void run() {
        while(true){
//            try{
//
//            }catch (InterruptedException e){
//                e.printStackTrace();
//                if(mQuit)
//                    return;
//                continue;
//            }
        }
    }
}
