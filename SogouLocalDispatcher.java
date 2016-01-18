package sogou.mobile.base.sogou_volley;

import android.os.Process;

import java.util.concurrent.PriorityBlockingQueue;

import sogou.mobile.explorer.util.LogUtil;

/**
 * Created by liuyu on 16/1/4.
 */
public class SogouLocalDispatcher extends SogouDispatcherThread{

    private PriorityBlockingQueue<SogouLocalRequest<?>> mLocalQueue;
    private ResponseDelivery mDelivery;

    public SogouLocalDispatcher(PriorityBlockingQueue<SogouLocalRequest<?>> queue,
                                ResponseDelivery delivery){
        mLocalQueue = queue;
        mDelivery = delivery;
    }

    @Override
    public void run() {

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while(true){

            SogouLocalRequest<?> request = null;
            try{
                request = mLocalQueue.take();
                request.addMarker("task-local");

                if(request.isCanceled()){
                    request.finish();
                    continue;
                }
                LogUtil.e("hello world from Thread：："+Thread.currentThread().getId());
                SogouResponse<?> response = request.requestBody();
                //TODO 将没法通用缓存的步骤暂时放在此处
                request.onRequestNext(response);
                request.addMarker("task-local-finish");

                request.markDelivered();
                mDelivery.postResponse(request, response);

            }catch (InterruptedException e){
                if(mQuit)
                    return;
                continue;
            }

            try{

            }catch (Exception e){
                SogouBaseError error = new SogouBaseError(e);
                mDelivery.postError(request, error);
            }
        }
    }

}
