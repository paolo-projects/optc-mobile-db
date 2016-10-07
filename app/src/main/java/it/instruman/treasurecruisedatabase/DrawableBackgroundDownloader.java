package it.instruman.treasurecruisedatabase;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Paolo on 06/10/2016.
 */

public class DrawableBackgroundDownloader {

    private static int MAX_CACHE_SIZE = 80;
    private final Map<String, SoftReference<Drawable>> mCache = new HashMap<>();
    private final LinkedList<Drawable> mChacheController = new LinkedList<>();
    private final Map<ImageView, String> mImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private int THREAD_POOL_SIZE = 3;
    private ExecutorService mThreadPool;

    /**
     * Constructor
     */
    public DrawableBackgroundDownloader() {
        mThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }


    /**
     * Clears all instance data and stops running threads
     */
    public void Reset() {
        ExecutorService oldThreadPool = mThreadPool;
        mThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        oldThreadPool.shutdownNow();

        mChacheController.clear();
        mCache.clear();
        mImageViews.clear();
    }

    public void loadDrawable(final String url, final ImageView imageView, Drawable placeholder) {
        mImageViews.put(imageView, url);
        Drawable drawable = getDrawableFromCache(url);

        // check in UI thread, so no concurrency issues
        if (drawable != null) {
            //Log.d(null, "Item loaded from mCache: " + url);
            imageView.setImageDrawable(drawable);
        } else {
            imageView.setImageDrawable(placeholder);
            queueJob(url, imageView, placeholder);
        }
    }


    private Drawable getDrawableFromCache(String url) {
        if (mCache.containsKey(url)) {
            return mCache.get(url).get();
        }

        return null;
    }

    private synchronized void putDrawableInCache(String url, Drawable drawable) {
        int chacheControllerSize = mChacheController.size();
        if (chacheControllerSize > MAX_CACHE_SIZE)
            mChacheController.subList(0, MAX_CACHE_SIZE / 2).clear();

        mChacheController.addLast(drawable);
        mCache.put(url, new SoftReference<>(drawable));

    }

    private void queueJob(final String url, final ImageView imageView, final Drawable placeholder) {
    /* Create handler in UI thread. */
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String tag = mImageViews.get(imageView);
                if (tag != null && tag.equals(url)) {
                    if (imageView.isShown())
                        if (msg.obj != null) {
                            imageView.setImageDrawable((Drawable) msg.obj);
                        } else {
                            imageView.setImageDrawable(placeholder);
                            //Log.d(null, "fail " + url);
                        }
                }
            }
        };

        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                final Drawable bmp = downloadDrawable(url);
                // if the view is not visible anymore, the image will be ready for next time in cache
                if (imageView.isShown()) {
                    Message message = Message.obtain();
                    message.obj = bmp;
                    //Log.d(null, "Item downloaded: " + url);

                    handler.sendMessage(message);
                }
            }
        });
    }


    private Drawable downloadDrawable(String url) {
        try {
            InputStream is = getInputStream(url);

            Drawable drawable = Drawable.createFromStream(is, url);
            putDrawableInCache(url, drawable);
            return drawable;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private InputStream getInputStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection;
        connection = url.openConnection();
        connection.setUseCaches(true);
        connection.connect();
        return connection.getInputStream();
    }
}