package com.funakoshi.resolveInfoAsyncLoader.impl;

import android.content.pm.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by max on 30.09.15.
 */
public class SaveResolveInfoTask implements Runnable {

    private ResolveInfo resolveInfo;
    private Callback callback;
    private File file;
    private PackageManager packageManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Stack<SaveResolveInfoTask> reusedOldTaskStack;
    private volatile AtomicBoolean isCanceled = new AtomicBoolean(false);

    public SaveResolveInfoTask(PackageManager pm, ResolveInfo resolveInfo, Callback callback,
            File file, Stack<SaveResolveInfoTask> reusedOldTaskStack) {
        packageManager = pm;
        this.resolveInfo = resolveInfo;
        this.callback = callback;
        this.file = file;
        this.reusedOldTaskStack = reusedOldTaskStack;
    }

    public ResolveInfo getResolveInfo() {
        return resolveInfo;
    }

    public void setResolveInfo(ResolveInfo resolveInfo) {
        this.resolveInfo = resolveInfo;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setIsCanceled(boolean value) {
        isCanceled = new AtomicBoolean(value);
    }

    public void cancel() {
        isCanceled = new AtomicBoolean(true);
        callback.cancel();
        this.file = null;
        this.callback = null;
        this.resolveInfo = null;
        reusedOldTaskStack.push(this);
    }

    @Override
    public void run() {
        if (!isCanceled.get()) {
            if (!file.exists()) {
                saveInFile(file, resolveInfo.loadIcon(packageManager));
            } else {
                //do nothing
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!isCanceled.get()) {
                        if (file.exists() && file.canRead()) {
                            callback.save(Uri.fromFile(file));
                        } else {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isCanceled.get()) {
                                        callback.save(Uri.fromFile(file));
                                    }
                                }
                            }, 50);
                        }

                    }
                }
            });
        }
    }

    private void saveInFile(File file, Drawable drawable) {
        Bitmap bitmap = drawableToBitmap(drawable);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            file.setReadable(false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 30, out);
            out.flush();
            file.setReadable(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }

    }

    private Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                if (bitmapDrawable.getIntrinsicHeight() > 96 ||
                        bitmapDrawable.getIntrinsicWidth() > 96) {
                    return Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 96, 96, false);
                }
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
