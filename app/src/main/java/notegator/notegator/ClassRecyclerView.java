package notegator.notegator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassRecyclerView extends AppCompatActivity {

    private EditText addClassText;
    private ImageButton addClassButton;
    private RecyclerView classRecyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ClassAdapter classAdapter;
    private List<String> userClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_recycler_view);
        addClassText = (EditText)findViewById(R.id.add_class_text);
        addClassButton = (ImageButton)findViewById(R.id.add_class_button);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classString = addClassText.getText().toString().toUpperCase();
                if(!classString.isEmpty()){
                    userClasses.add(classString);
                    Map<String, Object> classMap = new HashMap<>();
                    classMap.put("classes", userClasses);
                    db.collection("user").document(mAuth.getUid())
                            .update(classMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            addClassText.setHint("Class code (e.g. COP3502)");
                        }
                    });
                }
            }
        });
        getUserClasses();
    }

    private void getUserClasses() {
        userClasses = new ArrayList<String>();
        String uid = mAuth.getUid();
        CollectionReference collectionReference = db.collection("user");
        Query query = collectionReference.whereEqualTo("uid", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        userClasses = (ArrayList<String>) document.get("classes");
                    }
                    //Callback
                    populateList();
                }
            }
        });
    }

    private void populateList() {
        classAdapter = new ClassAdapter(userClasses);
        classRecyclerView = (RecyclerView) findViewById(R.id.class_list);
        classRecyclerView.setHasFixedSize(true);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        classRecyclerView.setAdapter(classAdapter);
        classAdapter.notifyDataSetChanged();
    }
}
