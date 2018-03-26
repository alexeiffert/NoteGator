package notegator.notegator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alex on 3/20/2018.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private List<GroupListItem> list;
    private Context context;

    public GroupListAdapter(List<GroupListItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupListItem groupListItem = list.get(position);
        //TODO this is causing the app to crash... not sure why
        holder.setTextViewHeader(groupListItem.getHeader());
        holder.setTextViewText(groupListItem.getText());
        System.out.println(groupListItem.getHeader());
        System.out.println(groupListItem.getText());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewHeader;
        private TextView textViewText;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewHeader = (TextView) itemView.findViewById(R.id.header);
            textViewText = (TextView) itemView.findViewById(R.id.text);
        }
        public void setTextViewHeader(String text) {
            textViewHeader.setText(text);
        }
        public void setTextViewText(String text){
            textViewText.setText(text);
        }

    }
}
