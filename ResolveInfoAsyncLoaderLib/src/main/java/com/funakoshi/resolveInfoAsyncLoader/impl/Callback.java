package com.funakoshi.resolveInfoAsyncLoader.impl;

import android.net.Uri;

/**
 * Created by max on 30.09.15.
 */
public abstract class Callback {

    private boolean isCanceled = false;

    public final void save(Uri uri) {
        if (!isCanceled) {
            onSave(uri);
        } else {
            //doNothing
        }
    }

    protected abstract void onSave(Uri uri);

    public final void cancel() {
        isCanceled = true;
    }

}
