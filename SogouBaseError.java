package sogou.mobile.base.sogou_volley;

/**
 * Created by liuyu on 16/1/4.
 */
public class SogouBaseError extends Exception{

    public final SogouResponse mResponse;

    public SogouBaseError(){
        mResponse = null;
    }

    public SogouBaseError(SogouResponse response){
        mResponse = response;
    }

    public SogouBaseError(String exceptionMsg){
        super(exceptionMsg);
        mResponse = null;
    }

    public SogouBaseError(String exceptionMsg, Throwable reason){
        super(exceptionMsg, reason);
        mResponse = null;
    }

    public SogouBaseError(Throwable reason){
        super(reason);
        mResponse = null;
    }
}
