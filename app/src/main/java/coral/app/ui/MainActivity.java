package coral.app.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import coral.app.Constants;
import coral.app.R;
import coral.app.adapters.ImagesAdapter;


public class MainActivity extends AppCompatActivity {

    private static final String LOG = "MainActivity";
    private static final int RC_WRITE_STORAGE = 101;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_recycler_view);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view -> {
            CropImage.activity(null)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this);
        }));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshView);
        swipeRefreshLayout.setOnRefreshListener(this::showFiles);


        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            CropImage.activity(null)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this);
        }

        showFiles();
    }

    private void showFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {

            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        RC_WRITE_STORAGE
                );
            } else {
                Snackbar snackbar = Snackbar.make(
                        swipeRefreshLayout,
                        R.string.storage_permission_rationale,
                        Snackbar.LENGTH_LONG
                );

                snackbar.setAction(R.string.grant,
                        v -> requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                RC_WRITE_STORAGE
                        )
                );
                snackbar.show();
                showEmptyView(true);
            }
        } else {
            String filePath = "/" + getString(R.string.app_name);
            File root = Environment.getExternalStorageDirectory();
            String path = root.toString() + filePath;
            File directory = new File(path);

            if (!directory.exists()) {
                File newFile = new File(root, filePath);
                newFile.mkdirs();
                showEmptyView(true);
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                return;
            }

            File[] files = directory.listFiles();
            if (files.length == 0) {
                showEmptyView(true);
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                return;
            }

            showEmptyView(false);
            int orientation = getResources().getConfiguration().orientation;
            int spanCount;
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                spanCount = 2;
            else
                spanCount = 3;

            recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
            recyclerView.setAdapter(new ImagesAdapter(this, files));
        }

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }


    private void showEmptyView(boolean show) {
        TextView emptyView = findViewById(R.id.emptyView);
        if (show)
            emptyView.setVisibility(View.VISIBLE);
        else
            emptyView.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Intent intent = new Intent(this, TransformImageActivity.class);
                intent.putExtra(Constants.EXTRA_IMAGE, resultUri);
                startActivity(intent);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(LOG, error.toString());
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RC_WRITE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFiles();
            } else {
                Snackbar.make(swipeRefreshLayout,
                        R.string.storage_permission_rationale,
                        Snackbar.LENGTH_LONG
                ).show();
                showEmptyView(true);
            }
        }
    }
}
