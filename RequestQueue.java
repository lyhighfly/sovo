package sogou.mobile.base.sogou_volley;


import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liuyu on 16/1/4.
 */
public class RequestQueue {

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    private static final int DEFAULT_THREAD_POOL_SIZE = 3;
    private int mPoolSize;

    private SogouTaskDispatcher mDispatcher;

    private Cache mCache;
    private ResponseDelivery mDelivery;

    private final Set<SogouBaseRequest<?>> mCurrentRequests = new HashSet<SogouBaseRequest<?>>();
//
//    private Map<String, Queue<SogouBaseRequest<?>>> mWaitingRequests =
//            new HashMap<String, Queue<SogouBaseRequest<?>>>();

    private final PriorityBlockingQueue<SogouBaseRequest<?>> mQueue =
            new PriorityBlockingQueue<SogouBaseRequest<?>>();


    public RequestQueue(Cache cache){
        this(cache, DEFAULT_THREAD_POOL_SIZE);
    }

    public RequestQueue(Cache cache, int threadPoolSize){
        this(cache, threadPoolSize, new ResponseDeliveryImpl(new Handler(Looper.getMainLooper())));
    }

    public RequestQueue(Cache cache, int threadPoolSize, ResponseDelivery delivery){
        mCache = cache;
        mPoolSize = threadPoolSize;
        mDelivery = delivery;
    }

    public void start(){
        stop();
        if(mDispatcher == null)
            mDispatcher = new SogouTaskDispatcher(mQueue, mCache, mPoolSize, mDelivery);
        mDispatcher.startReally();
    }

    public void stop(){
        if(mDispatcher != null) {
            mDispatcher.quit();
            mDispatcher = null;
        }
    }

    public int getSequenceNumber(){
        return mSequenceGenerator.incrementAndGet();
    }

    public void cancelAll(){
        synchronized (mCurrentRequests){
            for(SogouBaseRequest request : mCurrentRequests){
                request.cancel();
            }
        }
    }

    public SogouBaseRequest add(SogouBaseRequest<?> request){
        request.setRequestQueue(this);
        synchronized(mCurrentRequests){
            mCurrentRequests.add(request);
        }
        request.setSequenceID(getSequenceNumber());
        request.addMarker("add-to-queue");

        //avoid multi same request in very short time, whatever local or net request
//        synchronized (mWaitingRequests){
//            SogouBaseRequest req = (SogouBaseRequest)request;
//            String cacheKey = req.getCacheKey();
//            if(mWaitingRequests.containsKey(cacheKey)){
//                Queue<SogouBaseRequest<?>> statedRequests = mWaitingRequests.get(cacheKey);
//                if(statedRequests == null){
//                    statedRequests = new LinkedList<SogouBaseRequest<?>>();
//                }
//                statedRequests.add(req);
//                mWaitingRequests.put(cacheKey, statedRequests);
//            }else{
//                mWaitingRequests.put(cacheKey, null);
//                mQueue.add(request);
//            }
//        }
        mQueue.add(request);
        return request;
    }

    //not be same with volley in mWaitingQueue process
    public void finish(SogouBaseRequest<?> request){
        synchronized (mCurrentRequests){
            mCurrentRequests.remove(request);
        }
//        synchronized (mWaitingRequests){
//            String cacheKey = request.getCacheKey();
//            Queue<SogouBaseRequest<?>> waitingQueue = mWaitingRequests.get(cacheKey);
//            // volley add waiting queue to  cache queue...
//        }

    }
}
