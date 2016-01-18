package sogou.mobile.base.sogou_volley;

/**
 * Created by liuyu on 16/1/5.
 */
public interface SogouNetWork {

    public SogouFrameWorkResponse performRequest(SogouNetRequest<?> request) throws SogouBaseError;
}
