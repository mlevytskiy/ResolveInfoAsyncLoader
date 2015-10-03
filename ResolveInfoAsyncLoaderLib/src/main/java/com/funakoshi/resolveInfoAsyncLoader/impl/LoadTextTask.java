package com.funakoshi.resolveInfoAsyncLoader.impl;

import android.content.pm.*;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by max on 01.10.15.
 */
public class LoadTextTask implements Runnable {

    private Handler handler = new Handler(Looper.getMainLooper());
    private ResolveInfo resolveInfo;
    private PackageManager packageManager;
    private volatile AtomicBoolean isCanceled = new AtomicBoolean(false);
    private TextView textView;
    private Map<String, String> map;
    private Stack<LoadTextTask> reusedOldTaskStack;

    public LoadTextTask(TextView textView, ResolveInfo resolveInfo, PackageManager pm,
                        Map<String, String> map, Stack<LoadTextTask> reusedOldTaskStack) {
        this.resolveInfo = resolveInfo;
        packageManager = pm;
        this.textView = textView;
        this.map = map;
        this.reusedOldTaskStack = reusedOldTaskStack;
    }

    public void setIsCanceled(boolean isCanceled) {
        this.isCanceled = new AtomicBoolean(isCanceled);
    }

    public void setResolveInfo(ResolveInfo resolveInfo) {
        this.resolveInfo = resolveInfo;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void run() {
        if (!isCanceled.get()) {
            CharSequence charSequence = resolveInfo.loadLabel(packageManager);
            map.put(resolveInfo.activityInfo.name, charSequence.toString());
            handler.post(new RunCallbackTask(resolveInfo.activityInfo.name));
        } else {
            textView = null;
            resolveInfo = null;
            reusedOldTaskStack.push(this);
        }
    }


    private class RunCallbackTask implements Runnable {

        private String activityInfoName;

        public RunCallbackTask(String activityInfoName) {
            this.activityInfoName = activityInfoName;
        }

        @Override
        public void run() {
            if (!isCanceled.get()) {
                textView.setText(map.get(activityInfoName));
            }
        }
    }
}
