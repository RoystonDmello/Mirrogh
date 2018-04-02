package coral.app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import coral.app.R;
import coral.app.models.StyleModel;

public class StylesAdapter extends RecyclerView.Adapter<StylesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<StyleModel> stylesArrayList;
    private StylesAdapter.OnStyleSelectedListener listener;

    public StylesAdapter(Context context, ArrayList<StyleModel> stylesArrayList,
                         OnStyleSelectedListener listener) {
        this.context = context;
        this.stylesArrayList = stylesArrayList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_item_style, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String styleName = stylesArrayList.get(position).getId().replace(".ckpt", "").replace("_", " ");
        holder.textView.setText(styleName);
        Picasso.get().load(stylesArrayList.get(position).getThumbnailUrl())
                .error(R.drawable.ic_image)
                .into(holder.thumbnailView);
        holder.rootView.setOnClickListener(v -> listener.onStyleSelected(styleName));
    }


    @Override
    public int getItemCount() {
        return stylesArrayList.size();
    }

    public interface OnStyleSelectedListener {
        void onStyleSelected(String style);
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
