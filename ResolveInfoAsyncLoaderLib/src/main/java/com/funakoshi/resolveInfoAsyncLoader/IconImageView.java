package com.funakoshi.resolveInfoAsyncLoader;

import android.content.*;
import android.content.pm.*;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.funakoshi.resolveInfoAsyncLoader.impl.Callback;
import com.funakoshi.resolveInfoAsyncLoader.impl.ExecutorHolder;
import com.funakoshi.resolveInfoAsyncLoader.impl.FileGenerator;
import com.funakoshi.resolveInfoAsyncLoader.impl.SaveResolveInfoTask;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 * Created by max on 01.10.15.
 */
public class IconImageView extends ImageView {

    private static WeakReference<Drawable> defaultDrawable;
    private static Stack<SaveResolveInfoTask> reusedOldTaskStack = new Stack<>();

    private SaveResolveInfoTask task;

    public IconImageView(Context context) {
        super(context);
    }

    public IconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setResolveInfo(ResolveInfo resolveInfo) {
        cancelCurrentTask();
        FileGenerator fileGenerator = new FileGenerator(getContext());
        final File file = fileGenerator.generate(resolveInfo);
        if (file.exists()) {
            if (file.canRead()) {
                this.setImageURI(Uri.fromFile(file));
            } else {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setImageURI(Uri.fromFile(file));
                    }
                }, 200);
            }
        } else {
            Drawable defaultLoading = getDefaultDrawable();
            this.setImageDrawable(defaultLoading);
            task = getTask(resolveInfo, getContext().getPackageManager(),
            new Callback() {
                @Override
                public void onSave(final Uri uri) {
                    if (file.exists() || file.canRead()) {
                        setImageURI(uri);
                    } else {
                        getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setImageURI(uri);
                            }
                        }, 200);
                    }
                }
                    }, file);
            ExecutorHolder.STACK_EXECUTOR.execute(task);
        }
    }

    public Handler getHandler() {
        Handler handler = super.getHandler();
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    private SaveResolveInfoTask getTask(ResolveInfo resolveInfo, PackageManager pm, Callback callback, File file) {
        SaveResolveInfoTask task;
        if (reusedOldTaskStack.isEmpty()) {
            task = new SaveResolveInfoTask(pm, resolveInfo, callback, file, reusedOldTaskStack);
        } else {
            task = reusedOldTaskStack.pop();
            task.setResolveInfo(resolveInfo);
            task.setCallback(callback);
            task.setFile(file);
            task.setIsCanceled(false);
        }
        return task;
    }

    private Drawable getDefaultDrawable() {
        if (defaultDrawable == null || defaultDrawable.get() == null) {
            defaultDrawable = new WeakReference<Drawable>(new ColorDrawable(0xFFF5F5F5));
        }
        return defaultDrawable.get();
    }

    public void cancelCurrentTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelCurrentTask();
        reusedOldTaskStack.clear();
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

}
