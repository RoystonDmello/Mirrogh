package royston_dmello.mirrogh.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
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
import android.widget.Toast;

import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import royston_dmello.mirrogh.Constants;
import royston_dmello.mirrogh.R;
import royston_dmello.mirrogh.models.StyleModel;
import royston_dmello.mirrogh.adapters.StylesAdapter;
import util.RestClient;

public class TransformImageActivity extends AppCompatActivity {

    private Uri imageURI = null;
    private static final String LOG = "TransformActivity";
    private ImageView imageView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform_image);

        recyclerView = findViewById(R.id.recyclerView);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        imageURI = intent.getParcelableExtra(Constants.EXTRA_IMAGE);
        Picasso.get().load(imageURI).into(imageView);

        showStyles();
    }

    //TODO: implement this
    private void showStyles(){
        ArrayList<StyleModel> styles =  new ArrayList<>();

        for(int i=0;i<15;i++) styles.add(new StyleModel());
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(new StylesAdapter(this,styles));

    }

    private void uploadImage(View view) {
        String IMAGE = "image";

        RequestParams params = new RequestParams();

        try {
            File imageFile = new File(getRealPathFromURI(this.imageURI));
            System.out.print(getRealPathFromURI(this.imageURI));
            params.put(IMAGE, imageFile);
            //params.put(IMAGE, "temp");

            final ProgressDialog progressDialog = new ProgressDialog(TransformImageActivity.this);
            progressDialog.setCancelable(false);

            RestClient.setTimeOut(480000);

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
                    Log.d(getLocalClassName(), response.toString());



                    String base64String = null;
                    try {
                        base64String = response.getString("image_string");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String base64Image = base64String;

                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    ImageView imageViewLoad = findViewById(R.id.imageView);
                    imageViewLoad.setImageBitmap(decodedByte);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
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
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
