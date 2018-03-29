package royston_dmello.mirrogh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import royston_dmello.mirrogh.R;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder>{

    private Context context;
    private File[] files;

    public ImagesAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_image,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File file = files[position];
        Picasso.get().load(file).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
