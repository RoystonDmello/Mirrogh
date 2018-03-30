package royston_dmello.mirrogh.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import royston_dmello.mirrogh.Constants;
import royston_dmello.mirrogh.R;
import royston_dmello.mirrogh.adapters.StylesAdapter;
import royston_dmello.mirrogh.models.StyleModel;
import util.RestClient;

public class TransformImageActivity extends AppCompatActivity {

    private Uri imageURI = null;
    private static final String LOG = "TransformActivity";
    private ImageView imageView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform_image);

        recyclerView = findViewById(R.id.recyclerView);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.styles_loading_progress_bar);

        Intent intent = getIntent();
        imageURI = intent.getParcelableExtra(Constants.EXTRA_IMAGE);
        Picasso.get().load(imageURI).into(imageView);

        showStyles();
    }

    //TODO: implement this
    private void showStyles() {
        ArrayList<StyleModel> styles = new ArrayList<>();

        RestClient.get("styles/get/", null, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject styleObject = response.getJSONObject(i);
                        String name = styleObject.getString("name");
                        String thumbnail = styleObject.getString("thumbnail");
                        styles.add(new StyleModel(name, thumbnail));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                recyclerView.setLayoutManager(
                        new LinearLayoutManager(TransformImageActivity.this, LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(new StylesAdapter(TransformImageActivity.this, styles));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TransformImageActivity.this, R.string.error_fetching_styles, Toast.LENGTH_LONG).show();
                if (errorResponse != null)
                    Log.d(getLocalClassName(), errorResponse.toString());
                else Log.d(getLocalClassName(), "Null response" +
                        "");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(TransformImageActivity.this, R.string.error_fetching_styles, Toast.LENGTH_LONG).show();
                Log.d(getLocalClassName(), responseString);
            }
        });

    }

    private void uploadImage() {
        String IMAGE = "image";

        RequestParams params = new RequestParams();

        try {
            File imageFile = new File(getRealPathFromURI(this.imageURI));
            System.out.print(getRealPathFromURI(this.imageURI));
            params.put(IMAGE, imageFile);

            final ProgressDialog progressDialog = new ProgressDialog(TransformImageActivity.this);
            progressDialog.setCancelable(false);

            RestClient.setTimeOut(300000);

            RestClient.post("transform/", null, params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    progressDialog.setMessage(getString(R.string.transforming));
                    progressDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.dismiss();
                    RestClient.setTimeOut(10000);
                    Log.d(getLocalClassName(), response.toString());

                    try {
                        String base64String = response.getString("image_string");

                        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        ImageView imageViewLoad = findViewById(R.id.imageView);
                        imageViewLoad.setImageBitmap(decodedByte);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
                    RestClient.setTimeOut(10000);
                    if (errorResponse != null)
                        Log.e(LOG, errorResponse.toString());
                    else Log.e(LOG, throwable.getMessage());
                    Toast.makeText(TransformImageActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
