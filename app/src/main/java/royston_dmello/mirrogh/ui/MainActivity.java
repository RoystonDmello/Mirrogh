package royston_dmello.mirrogh.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import royston_dmello.mirrogh.Constants;
import royston_dmello.mirrogh.R;
import royston_dmello.mirrogh.adapters.ImagesAdapter;


public class MainActivity extends AppCompatActivity {

    private static final int IMG_RESULT = 1;
    private static final String LOG = "MainActivity";
    private static final int RC_READ_STORAGE = 101;
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
        swipeRefreshLayout.setOnRefreshListener(this::onResume);

        showFiles();
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setEnabled(false);
    }

    private void showFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_READ_STORAGE);
            } else {
                Snackbar snackbar = Snackbar.make(swipeRefreshLayout, R.string.storage_permission_rationale, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.grant, v -> requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_READ_STORAGE));
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
                return;
            }
            File[] files = directory.listFiles();
            if (files.length == 0) {
                showEmptyView(true);
                return;
            }
            showEmptyView(false);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter(new ImagesAdapter(this, files));
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_READ_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFiles();
            } else {
                Snackbar.make(swipeRefreshLayout, R.string.storage_permission_rationale, Snackbar.LENGTH_LONG).show();
                showEmptyView(true);
            }
        }
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setEnabled(false);
    }
}
