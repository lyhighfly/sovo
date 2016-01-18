package sogou.mobile.base.sogou_volley;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

/**
 * Created by liuyu on 16/1/4.
 */
public class SoVolley {

    private static final String DEFAULT_CACHE_DIR = "sovolley";

    public static RequestQueue newRequestQueue(Context context){

        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        String userAgent = "sovolley/0";

        try{
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionName;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        Cache cache = new SogouCacheImpl2();
//        Cache cache = new SogouCacheImpl();
        RequestQueue queue = new RequestQueue(cache);
        queue.start();
        return queue;
    }
}
