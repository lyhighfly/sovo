package sogou.mobile.base.sogou_volley;

/**
 * Created by liuyu on 16/1/4.
 */
public abstract class SogouLocalRequest<T> extends SogouBaseRequest{


    public SogouLocalRequest(SogouResponse.OnErrorListener listener){
        super(RequestType.LOCAL, listener);
    }

    //TODO 具体的事务
    protected abstract SogouResponse<T> requestBody();

    @Override
    protected SogouResponse parseResponse(SogouFrameWorkResponse response) {
        return null;
    }

    //local task must be unique key
    @Override
    public String getCacheKey() {
        return super.getCacheKey();
    }
}
