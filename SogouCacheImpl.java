package sogou.mobile.base.sogou_volley;

/**
 * Created by liuyu on 16/1/4.
 * @deprecated  no implement key method
 */
public class SogouCacheImpl implements Cache{


    public SogouCacheImpl(){


    }

    private void initCacheFile(){

    }

    @Override
    public Entry getEntry(SogouNetRequest key) {
        return null;
    }

    @Override
    public void putEntry(SogouNetRequest key, Entry entry) {

    }

    @Override
    public void removeEntry(Entry entry) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void invalidate(SogouNetRequest key, boolean fullExpire) {

    }

    @Override
    public void initialize() {

    }
}
