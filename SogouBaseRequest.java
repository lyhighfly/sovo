package sogou.mobile.base.sogou_volley;

import android.os.SystemClock;

/**
 * Created by liuyu on 16/1/4.
 */
public abstract class SogouBaseRequest<T> implements Comparable<SogouBaseRequest<T>>{

    protected RequestQueue mRequestQueue;


    protected SogouResponse.OnErrorListener mErrorListener;

    protected Integer mSequenceID;

    protected long mRequestBirthTime;

    protected boolean mResponseDelivered = false;
    public boolean mShouldCache = true;

    public String mCurrentMarker;
    public interface RequestType{
        int NET = 0;
        int LOCAL = 1;
    }

    public enum Priority{
        LOW,
        NORMAL,
        HIGH,
        IMEDIATE
    }

    protected int mType;
    protected boolean mCanceled = false;
    protected Priority mPriority = Priority.NORMAL;


    public SogouBaseRequest(int requestType, SogouResponse.OnErrorListener errorListener){
        mType = requestType;
        mErrorListener = errorListener;
    }

    public SogouBaseRequest(int requestType){
        this(requestType, null);
    }

    public void setRequestQueue(RequestQueue queue){
        mRequestQueue = queue;
    }

    public void setSequenceID(int ID){
        mSequenceID = ID;
    }

    public final int getSequenceID(){
        if(mSequenceID == null){
            throw new IllegalStateException("get Sequence called before set Sequence");
        }
        return mSequenceID;
    }

    public void addMarker(String tag){
        mCurrentMarker = tag;
        mRequestBirthTime = SystemClock.elapsedRealtime();
    }

    public int getRequestType(){
        return mType;
    }

    public void cancel(){
        mCanceled = true;
    }

    public boolean isCanceled(){
        return mCanceled;
    }

    public SogouResponse.OnErrorListener getErrorListener(){
        return mErrorListener;
    }

    public void markDelivered(){
        mResponseDelivered = true;
    }

    public boolean hasHadResponseDelivered(){
        return mResponseDelivered;
    }

    public Priority getPriority(){
        return mPriority;
    }

    public void setPriority(Priority p){
        mPriority = p;
    }

    public String getCacheKey(){
        return mSequenceID+"";
    }

    public boolean isShouldCache(){
        return mShouldCache;
    }

    @Override
    public int compareTo(SogouBaseRequest<T> another) {
        Priority left = this.getPriority();
        Priority right = another.getPriority();

        return left == right ?
                this.mSequenceID - another.mSequenceID
                : right.ordinal() - left.ordinal();
    }

    public void deliverError(SogouBaseError error){
        if(mErrorListener != null){
            mErrorListener.onErrorListener(error);
        }
    }

    //TODO params maybe wrong
    protected abstract SogouResponse<T> parseResponse(SogouFrameWorkResponse response);

    protected abstract void deliverResponse(T response);

    //TODO 将没法通用缓存的步骤暂时放在此处，可能将来需要与isShouldCached()配合，放置重复缓存操作
    protected void onRequestNext(SogouResponse<T> response){

    }

    protected void finish(){
        if(mRequestQueue != null)
            mRequestQueue.finish(this);
    }

    @Override
    public String toString() {
        return "BirthTime:"+mRequestBirthTime+"-- SqenceID:"+mSequenceID;
    }
}
