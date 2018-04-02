package coral.app.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import coral.app.R;
import coral.app.ui.ImageViewerActivity;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private Context context;
    private File[] files;

    public ImagesAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_image,
                parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File file = files[position];
        Picasso.get()
                .load(file)
                .error(R.drawable.ic_image)
                .into(holder.image);
        holder.image.setOnClickListener(v -> {
            v.setTransitionName("image");
            Intent intent = new Intent(context, ImageViewerActivity.class);
            intent.putExtra("file", file);
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context, v, "image").toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
