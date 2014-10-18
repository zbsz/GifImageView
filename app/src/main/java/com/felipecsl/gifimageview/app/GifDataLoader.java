package com.felipecsl.gifimageview.app;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class GifDataLoader extends AsyncTask<String, Void, byte[]> {

    private static final String TAG = "GifDataLoader";
    private final AssetManager assets;

    public GifDataLoader(Context context) {
        this.assets = context.getAssets();
    }

    @Override
    protected byte[] doInBackground(String... params) {
        try {
            return IOUtils.toByteArray(assets.open(params[0]));
        } catch (IOException e) {
            Log.e(TAG, "resource loading failed", e);
            return null;
        }
    }
}
