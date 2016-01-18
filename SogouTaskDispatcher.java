package sogou.mobile.base.sogou_volley;

import android.os.Process;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import sogou.mobile.explorer.util.LogUtil;

/**
 * Created by liuyu on 16/1/4.
 */
public class SogouTaskDispatcher extends SogouDispatcherThread{
    private final BlockingQueue<SogouBaseRequest<?>> mQueue;
    private final Cache mCache;
    private ResponseDelivery mDelivery;
    private SogouNetWork mNetWork;

    private int mPoolSize;

    private SogouLocalDispatcher mLocalDispatcher;
    private SogouCacheDispatcher mCacheDispatcher;
    private SogouNetDispatcher[] mNetWorkDispatchers;
    private RequestQueue mRequestQueue;

    private final PriorityBlockingQueue<SogouLocalRequest<?>> mLocalQueue =
            new PriorityBlockingQueue<SogouLocalRequest<?>>();

    private final PriorityBlockingQueue<SogouNetRequest<?>> mCacheQueue =
            new PriorityBlockingQueue<SogouNetRequest<?>>();

    private final PriorityBlockingQueue<SogouNetRequest<?>> mNetWorkQueue =
            new PriorityBlockingQueue<SogouNetRequest<?>>();

    public SogouTaskDispatcher(BlockingQueue<SogouBaseRequest<?>> queue, Cache cache,
                               int threadPoolSize, ResponseDelivery delivery){
        mQueue = queue;
        mCache = cache;
        mDelivery = delivery;
        mPoolSize = threadPoolSize;
        mNetWorkDispatchers = new SogouNetDispatcher[mPoolSize];
        mNetWork = new SogouBasicNetWork(new SogouHttpClientStack());
    }

    private void startAll(){
        stopAll();
        mLocalDispatcher = new SogouLocalDispatcher(mLocalQueue, mDelivery);
//        mCacheDispatcher = new SogouCacheDispatcher(mCacheQueue, mNetWorkQueue, mCache, mDelivery);
        mLocalDispatcher.startReally();
//        mCacheDispatcher.startReally();
        for(int i=0; i<mNetWorkDispatchers.length; i++){
            SogouNetDispatcher netDispatcher = new SogouNetDispatcher(mNetWorkQueue, mCache, mDelivery, mNetWork);
            mNetWorkDispatchers[i] = netDispatcher;
            mNetWorkDispatchers[i].startReally();
        }
    }

    @Override
    public void startReally() {
        startAll();
        super.startReally();
    }

    @Override
    public void quit() {
        stopAll();
        super.quit();
    }

    private void stopAll(){
        if(mLocalDispatcher != null)
            mLocalDispatcher.quit();
        if(mCacheDispatcher != null)
            mCacheDispatcher.quit();
        for(int i=0; i<mNetWorkDispatchers.length; i++){
            if(mNetWorkDispatchers[i] != null){
                mNetWorkDispatchers[i].quit();
            }
        }
    }

    @Override
    public void run() {

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mCache.initialize();
        while(true){
            try{
                SogouBaseRequest<?> request = mQueue.take();

                LogUtil.e("liuyu", "Task get:"+request);

                request.addMarker("task-dispatch");
                if(request.isCanceled()){
                    request.addMarker("task-canceled");
                    continue;
                }

                request.setRequestQueue(mRequestQueue);
                //TODO mWaitingQueue 短时间内重复请求的规避操作

                if(request.getRequestType() == SogouBaseRequest.RequestType.LOCAL){
                    request.addMarker("task-add-local");
                    mLocalQueue.add((SogouLocalRequest<?>) request);
                    continue;
                }

                final SogouNetRequest netRequest = (SogouNetRequest)request;
                Cache.Entry entry = mCache.getEntry(netRequest);
                if(entry == null){
                    netRequest.addMarker("task-cache-miss");
                    mNetWorkQueue.put(netRequest);
                    continue;
                }

                if(entry.isExpired()){
                    netRequest.addMarker("task-cache-hit-expired");
                    netRequest.setCacheEntry(entry);
                    mNetWorkQueue.put(netRequest);
                    continue;
                }

                netRequest.addMarker("task-cache-hit");
                SogouResponse<?> response = netRequest.parseResponse(new SogouFrameWorkResponse(entry.data));
                netRequest.addMarker("task-cache-hit-parsed");
                if(!entry.refreshNeeded()){
                    mDelivery.postResponse(netRequest, response);
                }else{
                    netRequest.addMarker("cache-hit-refresh-needed");
                    netRequest.setCacheEntry(entry);
                    netRequest.mIntermediate = true;
                    mDelivery.postResponse(netRequest, response, new Runnable() {
                        @Override
                        public void run() {

                            mNetWorkQueue.put(netRequest);
                        }
                    });
                }

            }catch(Exception e){
                if(mQuit){
                    return;
                }
                continue;
            }
        }
    }
}
