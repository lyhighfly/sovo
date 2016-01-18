package sogou.mobile.base.sogou_volley;

/**
 * Created by liuyu on 16/1/4.
 */
public interface ResponseDelivery {

    public void postResponse(SogouBaseRequest<?> request, SogouResponse<?> response);

    public void postResponse(SogouBaseRequest<?> request, SogouResponse<?> response, Runnable runnable);

    public void postError(SogouBaseRequest<?> request, SogouBaseError error);
}
