package royston_dmello.mirrogh.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import royston_dmello.mirrogh.R;
import royston_dmello.mirrogh.models.StyleModel;
import util.RestClient;

public class StylesAdapter extends RecyclerView.Adapter<StylesAdapter.ViewHolder> {
    private static final String LOG = "StylesAdapter";
    private Uri imageURI = null;
    private Context context;
    private ArrayList<StyleModel> stylesArraylist;
    private ImageView imageView;

    public StylesAdapter(Context context, ArrayList<StyleModel> stylesArraylist, Uri imageURI, ImageView imageView) {
        this.context = context;
        this.stylesArraylist = stylesArraylist;
        this.imageURI = imageURI;
        this.imageView = imageView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_item_style, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String styleName = stylesArraylist.get(position).getId();
        holder.textView.setText(styleName);
        Picasso.get().load(stylesArraylist.get(position).getThumbnail_url())
                .error(R.drawable.ic_image)
                .into(holder.thumbnailView);
        holder.rootView.setOnClickListener(v -> uploadImage(styleName));
    }

    private void uploadImage(String styleName) {
        String IMAGE = "image";
        String STYLE = "style";

        RequestParams params = new RequestParams();

        try {
            File imageFile = new File(getRealPathFromURI(this.imageURI));
            System.out.print(getRealPathFromURI(this.imageURI));
            params.put(IMAGE, imageFile);
            params.put(STYLE, styleName);

            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);

            RestClient.setTimeOut(300000);

            RestClient.post("transform/", null, params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    progressDialog.setMessage(context.getString(R.string.transforming));
                    progressDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.dismiss();
                    RestClient.setTimeOut(10000);
                    Log.d(LOG, response.toString());

                    try {
                        String base64String = response.getString("image_string");

                        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        imageView.setImageBitmap(decodedByte);

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
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return stylesArraylist.size();
    }

    private String getRealPathFromURI(Uri uri) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        View rootView;
        ImageView thumbnailView;

        ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textView = itemView.findViewById(R.id.styleName);
            thumbnailView = itemView.findViewById(R.id.style_thumbnail);
        }
    }

}
