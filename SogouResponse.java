package sogou.mobile.base.sogou_volley;

/**
 * Created by liuyu on 16/1/4.
 */
public class SogouResponse<T> {

    public final T result;
    public final Cache.Entry cacheEntry;
    public final SogouBaseError error;
    public boolean intermediate = false;

    public interface  Listener<T>{
        public void onResponse(T response);
    }

    public static <T> SogouResponse<T> success(T result, Cache.Entry entry){
        return new SogouResponse<T>(result, entry);
    }

    public static <T> SogouResponse<T> error(SogouBaseError error){
        return new SogouResponse<T>(error);
    }

    private OnErrorListener errorListener;

    public interface OnErrorListener{

        void onErrorListener(SogouBaseError error);
    }

    public void setOnErrorListener(OnErrorListener errListener){
        errorListener = errListener;
    }

    public boolean isSuccess(){
        return error == null;
    }

    private SogouResponse(T result, Cache.Entry cacheEntry){
        this.result = result;
        this.cacheEntry = cacheEntry;
        this.error = null;
    }

    private SogouResponse(SogouBaseError error){
        this.result = null;
        this.cacheEntry = null;
        this.error = error;
    }
}
