package com.funakoshi.resolveInfoAsyncLoader;

import android.content.*;
import android.content.pm.*;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.funakoshi.resolveInfoAsyncLoader.impl.ExecutorHolder;
import com.funakoshi.resolveInfoAsyncLoader.impl.LoadTextTask;

import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

/**
 * Created by max on 01.10.15.
 */
public class LabelTextView extends TextView {

    private static final String SHARED_PREFERENCE_NAME = "ResolveInfoAllLabels";
    private static final String LOADING = "Loading...";
    private LoadTextTask task;
    private static Map<String, String> map = new WeakHashMap<>();
    private static boolean mapIsSaved = false;
    private static Stack<LoadTextTask> reusedOldTaskStack = new Stack<>();

    public LabelTextView(Context context) {
        super(context);
    }

    public LabelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPackageName(String packageName) {
        Intent intent = new Intent();
        intent.setPackage(packageName);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
        setResolveInfo(resolveInfo, pm);
    }

    public void setResolveInfo(ResolveInfo resolveInfo) {
        setResolveInfo(resolveInfo, getContext().getPackageManager());
    }

    private void setResolveInfo(ResolveInfo resolveInfo, PackageManager pm) {
        if (map.isEmpty()) {
            SharedPreferences keyValues = getContext().getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
            Map<String, ?> map1 = keyValues.getAll();
            for (Map.Entry<String, ?> entry : map1.entrySet()) {
                map.put(entry.getKey(), (String) entry.getValue());
            }
        }
        mapIsSaved = !map.isEmpty();
        cancelCurrentTask();
        String value = map.get(resolveInfo.activityInfo.name);
        if (TextUtils.isEmpty(value)) {
            setText(LOADING);
            task = getTask(resolveInfo, pm, map);
            ExecutorHolder.STACK_EXECUTOR.execute(task);
        } else {
            setText(value);
        }
    }

    private LoadTextTask getTask(ResolveInfo resolveInfo, PackageManager pm, Map<String, String> map) {
        LoadTextTask task;
        if (reusedOldTaskStack.isEmpty()) {
            task = new LoadTextTask(this, resolveInfo, pm, map, reusedOldTaskStack);
        } else {
            task = reusedOldTaskStack.pop();
            task.setResolveInfo(resolveInfo);
            task.setTextView(this);
            task.setIsCanceled(false);
        }
        return task;
    }

    public void cancelCurrentTask() {
        if (task != null) {
            task.setIsCanceled(true);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelCurrentTask();
        if (!mapIsSaved) {
            SharedPreferences keyValues = getContext().getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = keyValues.edit();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                editor.putString(entry.getKey(), entry.getValue()).apply();
            }
            mapIsSaved = true;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

}
