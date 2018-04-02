package coral.app.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import coral.app.BuildConfig;
import coral.app.R;

public class ImageViewerActivity extends Activity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler hideHandler = new Handler();
    private ImageView contentView;
    private final Runnable hideRunnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View controlsView;
    private final Runnable showRunnable = new Runnable() {
        @Override
        public void run() {
            controlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean isVisible;
    private final Runnable mHideRunnable = this::hide;
    private final View.OnTouchListener mDelayHideTouchListener = this::onTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_viewer);
        isVisible = true;
        controlsView = findViewById(R.id.fullscreen_content_controls);
        contentView = findViewById(R.id.imageView);

        if (getIntent().getExtras() != null) {
            File file = (File) getIntent().getSerializableExtra("file");
            Picasso.get()
                    .load(file)
                    .error(R.drawable.ic_image)
                    .into(contentView);

            // Set up the user interaction to manually show or hide the system UI.
            contentView.setOnClickListener(view -> toggle());

            // Upon interacting with UI controls, delay any scheduled hide()
            // operations to prevent the jarring behavior of controls going away
            // while interacting with the UI.
            findViewById(R.id.share_button).setOnTouchListener(mDelayHideTouchListener);
            findViewById(R.id.close_button).setOnTouchListener(mDelayHideTouchListener);
            findViewById(R.id.share_button).setOnClickListener(v -> {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text, getString(R.string.app_name)));
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(ImageViewerActivity.this, BuildConfig.APPLICATION_ID + ".provider", file));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/jpeg");
                startActivity(Intent.createChooser(intent, "Share image via"));
            });
            findViewById(R.id.close_button).setOnClickListener(v -> onBackPressed());
        } else {
            finish();
        }
    }

    private void toggle() {
        if (isVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        controlsView.setVisibility(View.GONE);
        isVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showRunnable);
        hideHandler.postDelayed(hideRunnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        isVisible = true;

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hideRunnable);
        hideHandler.postDelayed(showRunnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        hideHandler.removeCallbacks(mHideRunnable);
        hideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private boolean onTouch(View view, MotionEvent motionEvent) {
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
        return false;
    }
}
