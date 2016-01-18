package sogou.mobile.base.sogou_volley;

import java.util.Collections;
import java.util.Map;

import sogou.mobile.explorer.ManageSpaceActivity;

/**
 * Created by liuyu on 16/1/4.
 */
public interface Cache {

    public static class Entry{

        public byte[] data;
        public String etag;
        public long serverDate;
        public long ttl;

        public long softTtl;

        public boolean isExpired = false;

        public String cacheFile;

        public Map<String, String> responseHeader = Collections.emptyMap();

        public boolean isExpired(){
            return isExpired;
        }

        //TODO  depend on softTTL
        public boolean refreshNeeded(){
            return false;
        }
    }

    public Entry getEntry(SogouNetRequest key);

    public void putEntry(SogouNetRequest key, Entry entry);

    public void removeEntry(Entry entry);

    public void clear();

    public void invalidate(SogouNetRequest key, boolean fullExpire);

    public void initialize();

}
