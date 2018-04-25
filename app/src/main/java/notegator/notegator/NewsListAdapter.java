package notegator.notegator;

/**
 * Created by Alex on 2/25/2018.
 */

import java.util.ArrayList;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewsListAdapter extends BaseExpandableListAdapter {

    private FirebaseStorage storage;
    private Context context;
    private ArrayList<HeaderInfo> classList;

    public NewsListAdapter(Context context, ArrayList<HeaderInfo> classList) {
        storage = FirebaseStorage.getInstance();

        this.context = context;
        this.classList = classList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<DetailInfo> productList =
                classList.get(groupPosition).getProductList();
        return productList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        DetailInfo detailInfo = (DetailInfo) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_row, null);
        }

        TextView date = (TextView) view.findViewById(R.id.date);
        date.setText(detailInfo.getDate());
        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(detailInfo.getName().trim());

        //Get the image from Firebase
        try {
            ImageView img = (ImageView) view.findViewById(R.id.thumbnail);
            StorageReference thumbnailReference = storage.getReferenceFromUrl(detailInfo.getThumbnail());
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(thumbnailReference)
                    .into(img);
        } catch(Exception e){
            Log.d("Load img", "Error");
        }

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        ArrayList<DetailInfo> productList =
                classList.get(groupPosition).getProductList();
        return productList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return classList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return classList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        HeaderInfo headerInfo = (HeaderInfo) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.group_heading, null);
        }

        TextView heading = (TextView) view.findViewById(R.id.heading);
        heading.setText(headerInfo.getName().trim());

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
