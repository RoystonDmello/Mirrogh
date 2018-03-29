package royston_dmello.mirrogh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import royston_dmello.mirrogh.R;
import royston_dmello.mirrogh.models.StyleModel;

public class StylesAdapter extends RecyclerView.Adapter<StylesAdapter.ViewHolder>{

    private Context context;
    private ArrayList<StyleModel> stylesArraylist;

    public StylesAdapter(Context context, ArrayList<StyleModel> stylesArraylist) {
        this.context = context;
        this.stylesArraylist = stylesArraylist;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.styleName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_item_style,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(stylesArraylist.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return stylesArraylist.size();
    }
}
