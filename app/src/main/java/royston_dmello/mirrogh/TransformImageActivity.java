package royston_dmello.mirrogh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;
import util.RestClient;

public class TransformImageActivity extends AppCompatActivity {

    private Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform_image);

        Intent showImage = getIntent();
        Uri URI = showImage.getParcelableExtra(MainActivity.EXTRA_IMAGE);

        this.imageURI = URI;

        String[] FILE = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(URI,
                FILE, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(FILE[0]);
        String ImageDecode = cursor.getString(columnIndex);
        cursor.close();

        ImageView imageViewLoad = (ImageView) findViewById(R.id.imageView);
        imageViewLoad.setImageBitmap(BitmapFactory
                .decodeFile(ImageDecode));
    }

    public void uploadImage(View view) {
        String IMAGE = "image";

        RequestParams params = new RequestParams();

        try {
            File imageFile = new File(getRealPathFromURI(this.imageURI));
            System.out.print(getRealPathFromURI(this.imageURI));
            params.put(IMAGE, imageFile);
//            params.put(IMAGE, "temp");

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

                    ImageView imageViewLoad = (ImageView) findViewById(R.id.imageView);
                    imageViewLoad.setImageBitmap(decodedByte);

//                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
                    if (errorResponse != null)
                        Log.e("TransformImageActivity", errorResponse.toString());
                    else Log.e("TransformImageActivity", throwable.getMessage());
                    Toast.makeText(TransformImageActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri uri) {
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

}
