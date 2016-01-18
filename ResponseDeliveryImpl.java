package sogou.mobile.base.sogou_volley;

import android.os.Handler;

import java.util.concurrent.Executor;

/**
 * Created by liuyu on 16/1/4.
 */
public class ResponseDeliveryImpl implements ResponseDelivery{

    private final Executor mResponsePoster;

    public ResponseDeliveryImpl(final Handler handler){
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    public ResponseDeliveryImpl(Executor executor){
        mResponsePoster = executor;
    }

    @Override
    public void postResponse(SogouBaseRequest<?> request, SogouResponse<?> response) {
        postResponse(request, response, null);
    }

    @Override
    public void postResponse(SogouBaseRequest<?> request, SogouResponse<?> response, Runnable runnable) {
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, runnable));
    }

    @Override
    public void postError(SogouBaseRequest<?> request, SogouBaseError error) {

    }

    private class ResponseDeliveryRunnable implements Runnable{

        private SogouBaseRequest mRequest;
        private SogouResponse mResponse;
        private Runnable mRunnable;

        public ResponseDeliveryRunnable(SogouBaseRequest request, SogouResponse response, Runnable runnable){
            mRequest = request;
            mResponse = response;
            mRunnable = runnable;
        }

        @Override
        public void run() {

            if(mRequest.isCanceled()){
                mRequest.addMarker("cancel-delivery-task");
                return;
            }

            if(mResponse.isSuccess()){
                mRequest.deliverResponse(mResponse);
            }else{
                mRequest.deliverError(mResponse.error);
            }

            if (mResponse.intermediate) {
                mRequest.addMarker("intermediate-response");
            } else {
                mRequest.finish();
            }

            if(mRunnable != null)
                mRunnable.run();
        }
    }
}
