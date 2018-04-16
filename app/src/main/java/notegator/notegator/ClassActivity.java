package notegator.notegator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClassActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Context hackContext;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<GroupListItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        isNotetaker(mAuth.getUid());

        configureRecyclerview();
        populateRecyclerview();
    }

    private void isNotetaker(String uid) {
        CollectionReference collectionReference = db.collection("user");
        Query query = collectionReference.whereEqualTo("uid", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean isNotetaker = false;
                if(task.isSuccessful()){
                    for(DocumentSnapshot document : task.getResult()) {
                        isNotetaker = (boolean)document.get("isNotetaker");
                    }
                }
                if(isNotetaker){
                    addFab();
                }
            }
        });
    }

    private void addFab(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddNotesActivity.class));
            }
        });
    }

    //Helper methods
    private void configureRecyclerview(){
        hackContext = this;
        db = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        list = new ArrayList<GroupListItem>();

        adapter = new GroupListAdapter(list, hackContext);
        recyclerView.setAdapter(adapter);
    }

    private void populateRecyclerview() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CollectionReference collectionReference = db.collection("message");
        /* TODO get messages
        for(String className : userClasses) {
            //TODO trouble with ordering by time
            Query query = collectionReference.whereEqualTo("class", className).limit(20);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String key = dataSnapshot.getKey().toString();
                            String text = dataSnapshot.getValue().toString();
                            GroupListItem groupListItem = new GroupListItem(key, text);
                            list.add(groupListItem);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
        */
    }
}
