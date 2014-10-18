package com.felipecsl.gifimageview.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.felipecsl.gifimageview.library.GifImageView;

import java.io.IOException;


public class MainActivity extends Activity implements View.OnClickListener {

    private GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gifImageView = (GifImageView) findViewById(R.id.gifImageView);
        final Button btnToggle = (Button) findViewById(R.id.btnToggle);

        btnToggle.setOnClickListener(this);

//        Debug.startMethodTracing("gif", 32 * 1024 * 1024);

        new GifDataLoader(getApplicationContext()) {
            @Override
            protected void onPostExecute(final byte[] bytes) {
                long time = SystemClock.uptimeMillis();
                gifImageView.setBytes(bytes);
                time = SystemClock.uptimeMillis() - time;
                Log.i("GifActivity", "setBytes time ms: " + time);
                gifImageView.startAnimation();
            }
        }.execute("earth.gif");

//        try {
//            for(String path : getAssets().list("")) {
//                Log.d("MainActivity", "found gif: " + path);
//
//                new GifDataLoader(getApplicationContext()) {
//                    @Override
//                    protected void onPostExecute(final byte[] bytes) {
//                        final GifImageView view = new GifImageView(MainActivity.this);
//                        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                        ((LinearLayout) findViewById(R.id.list)).addView(view);
//
//                        view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (view.isAnimating()) view.stopAnimation();
//                                else view.startAnimation();
//                            }
//                        });
//
//                        try {
//                            long time = SystemClock.uptimeMillis();
//                            view.setBytes(bytes);
//                            time = SystemClock.uptimeMillis() - time;
//                            Log.i("GifActivity", "setBytes time ms: " + time);
//                            view.startAnimation();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.execute(path);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        gifMovieView.setMovieResource(R.raw.earth);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Debug.stopMethodTracing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View v) {
        if (gifImageView.isAnimating())
            gifImageView.stopAnimation();
        else
            gifImageView.startAnimation();
    }
}
