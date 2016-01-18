package sogou.mobile.base.sogou_volley;

import android.os.Process;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by liuyu on 16/1/4.
 */
public class SogouNetDispatcher extends SogouDispatcherThread{

    private PriorityBlockingQueue<SogouNetRequest<?>> mNetWorkQueue;
    private Cache mCache;
    private ResponseDelivery mDelivery;
    private SogouNetWork mNetWork;

    public SogouNetDispatcher(PriorityBlockingQueue<SogouNetRequest<?>> netQ,
                              Cache cache, ResponseDelivery delivery, SogouNetWork netWork){
        mNetWorkQueue = netQ;
        mCache = cache;
        mDelivery = delivery;
        mNetWork = netWork;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        while(true){

            SogouNetRequest<?> request = null;

            try {
                request = mNetWorkQueue.take();
            }catch (InterruptedException e) {
                if (mQuit)
                    return;
                continue;
            }

            try{

                request.addMarker("net-task-take");
                if(request.isCanceled()){
                    request.finish();
                    continue;
                }

                SogouFrameWorkResponse response = mNetWork.performRequest(request);
                request.addMarker("net-task-http-complete");

                if(response.notModified && request.hasHadResponseDelivered()){
                    request.finish();
                    continue;
                }

                SogouResponse<?> detailResponse = request.parseResponse(response);

                //TODO 将没法通用缓存的步骤暂时放在此处
                request.onRequestNext(detailResponse);
                request.addMarker("net-task-parse-complete");

                if(request.isShouldCache() && request.getCacheEntry() != null){
                    mCache.putEntry(request, detailResponse.cacheEntry);
                    request.addMarker("net-task-cache-written");
                }

                request.markDelivered();
                mDelivery.postResponse(request, detailResponse);

            }catch (Exception e){
                if(mQuit)
                    return;
                SogouBaseError error = new SogouBaseError(e);
                mDelivery.postError(request, error);
                continue;
            }
        }
    }
}
