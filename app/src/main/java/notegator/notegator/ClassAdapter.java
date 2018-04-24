package notegator.notegator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kyle on 4/3/18.
 */

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    public List<String> classes;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public ClassAdapter(List<String> classes){
        this.classes = classes;
    }

    @Override
    public ClassAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_list_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClassAdapter.ViewHolder holder, final int position) {
        holder.className.setText(classes.get(position));
        holder.deleteClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deletedClass = classes.get(position);
                DocumentReference doc = mFirestore.collection("users").document(mUser.getUid());
                Map<String, Object> delete = new HashMap<>();
                delete.put("class" + deletedClass, FieldValue.delete());
                doc.update(delete);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public TextView className;
        public ImageButton deleteClass;
        public ViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            className = (TextView) mView.findViewById(R.id.class_name_list);
            deleteClass = (ImageButton) mView.findViewById(R.id.delete_class);
        }
    }
}
