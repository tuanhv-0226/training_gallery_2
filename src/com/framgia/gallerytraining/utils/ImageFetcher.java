package com.framgia.gallerytraining.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.framgia.gallerytraining.BuildConfig;

public class ImageFetcher extends ImageResizer {
	private static final String TAG = "ImageFetcher";
    private static final int DISK_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String DISK_CACHE_DIR = "disk_cache";
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private DiskLruCache diskCache;
    private File cacheDir;
    private boolean diskCacheStarting = true;
    private final Object diskCacheLock = new Object();
    private static final int DISK_CACHE_INDEX = 0;


	public ImageFetcher(Context context, int imageSize) {
		super(context, imageSize);
		init(context);
	}

	public ImageFetcher(Context context, int imageWidth, int imageHeight) {
        super(context, imageWidth, imageHeight);
        init(context);
    }

    private void init(Context context) {
        cacheDir = ImageCache.getDiskCacheDir(context, DISK_CACHE_DIR);
    }

    @Override
    protected void initDiskCacheInternal() {
        super.initDiskCacheInternal();
        initDiskCache();
    }

    private void initDiskCache() {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        synchronized (diskCacheLock) {
            if (ImageCache.getUsableSpace(cacheDir) > DISK_CACHE_SIZE) {
                try {
                    diskCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Disk cache initialized");
                    }
                } catch (IOException e) {
                    diskCache = null;
                }
            }
            diskCacheStarting = false;
            diskCacheLock.notifyAll();
        }
    }

    @Override
    protected void clearCacheInternal() {
        super.clearCacheInternal();
        synchronized (diskCacheLock) {
            if (diskCache != null && !diskCache.isClosed()) {
                try {
                    diskCache.delete();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Disk cache cleared");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "clearCacheInternal - " + e);
                }
                diskCache = null;
                diskCacheStarting = true;
                initDiskCache();
            }
        }
    }

    @Override
    protected void flushCacheInternal() {
        super.flushCacheInternal();
        synchronized (diskCacheLock) {
            if (diskCache != null) {
                try {
                    diskCache.flush();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Disk cache flushed");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "flush - " + e);
                }
            }
        }
    }

    @Override
    protected void closeCacheInternal() {
        super.closeCacheInternal();
        synchronized (diskCacheLock) {
            if (diskCache != null) {
                try {
                    if (!diskCache.isClosed()) {
                        diskCache.close();
                        diskCache = null;
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Disk cache closed");
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "closeCacheInternal - " + e);
                }
            }
        }
    }

    private Bitmap processBitmap(String data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "processBitmap - " + data);
        }

        final String key = ImageCache.hashKeyForDisk(data);
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        DiskLruCache.Snapshot snapshot;
        synchronized (diskCacheLock) {
            // Wait for disk cache to initialize
            while (diskCacheStarting) {
                try {
                    diskCacheLock.wait();
                } catch (InterruptedException e) {}
            }

            if (diskCache != null) {
                try {
                    snapshot = diskCache.get(key);
                    if (snapshot == null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "processBitmap, not found in disk cache, fetching...");
                        }
                        DiskLruCache.Editor editor = diskCache.edit(key);
                        if (editor != null) {
                            if (fetchFileToStream(data,
                                    editor.newOutputStream(DISK_CACHE_INDEX))) {
                                editor.commit();
                            } else {
                                editor.abort();
                            }
                        }
                        snapshot = diskCache.get(key);
                    }
                    if (snapshot != null) {
                        fileInputStream =
                                (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                        fileDescriptor = fileInputStream.getFD();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "processBitmap - " + e);
                } catch (IllegalStateException e) {
                    Log.e(TAG, "processBitmap - " + e);
                } finally {
                    if (fileDescriptor == null && fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {}
                    }
                }
            }
        }

        Bitmap bitmap = null;
        if (fileDescriptor != null) {
            bitmap = decodeSampledBitmapFromDescriptor(fileDescriptor, mImageWidth,
                    mImageHeight, getImageCache());
        }
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {}
        }
        return bitmap;
    }

    @Override
    protected Bitmap processBitmap(Object data) {
        return processBitmap(String.valueOf(data));
    }

    public boolean fetchFileToStream(String pathString, OutputStream outputStream) {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            in = new BufferedInputStream(new FileInputStream(pathString), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            Log.e(TAG, "Error in fetchBitmap - " + e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {}
        }
        return false;
    }

}
