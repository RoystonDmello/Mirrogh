package royston_dmello.mirrogh.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import royston_dmello.mirrogh.Constants;
import royston_dmello.mirrogh.R;
import royston_dmello.mirrogh.adapters.StylesAdapter;
import royston_dmello.mirrogh.models.StyleModel;
import util.RestClient;

public class TransformImageActivity extends AppCompatActivity {
    private static final String LOG = "TransformActivity";
    private Uri imageURI = null;
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
                recyclerView.setAdapter(new StylesAdapter(TransformImageActivity.this, styles, imageURI, imageView));
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
