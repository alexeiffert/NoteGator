package notegator.notegator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ClassActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String name;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle AB_toggle;
    private SwipeRefreshLayout refreshLayout;
    private Context hackContext;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<GroupListItem> list;

    private Button sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        checkIfLogged();  // Makes sure user is logged & updates name field
        isNotetaker(mAuth.getUid());

        drawerLayout = findViewById(R.id.drawerLayout);
        AB_toggle = new
                ActionBarDrawerToggle(this, drawerLayout,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(AB_toggle);
        AB_toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sendMessage = findViewById(R.id.send_message);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        configureRecyclerview();
        addMessageListener();

        configureSwipeRefresh();
    }

    public void sendMessage(){
        EditText message = findViewById(R.id.message);
        if(mAuth.getUid() != null){
            if(message.getText().toString() != null){
                HashMap<String, Object> newMessage = new HashMap<String, Object>();
                newMessage.put("uid", mAuth.getUid());
                newMessage.put("name", name);
                newMessage.put("time", new Date());
                newMessage.put("text", message.getText().toString());
                newMessage.put("class", "COP3502");

                CollectionReference collection = db.collection("message");
                collection.add(newMessage);
                message.setText("");
            }
        }
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupListAdapter(list, hackContext);
        recyclerView.setAdapter(adapter);
    }

    private void configureSwipeRefresh() {
        refreshLayout = findViewById(R.id.refreshClass);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.getAdapter().notifyDataSetChanged();
                refreshLayout.setRefreshing(false); //stop refresh animation when done;
            }
        });
    }

    private void addMessageListener(){
       CollectionReference reference = db.collection("message");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e == null) {
                    list.clear();
                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                        try {
                            DateFormat dateFormat = new SimpleDateFormat("MMM d, h:mm");
                            dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
                            String time = dateFormat.format(document.get("time"));
                            String header = document.get("name").toString();
                            String text = document.get("text").toString();
                            GroupListItem item = new GroupListItem(header, time, text);
                            list.add(item);
                        } catch (Exception e2) {
                            //TODO
                        }
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                }
            }
        });
    }

    private void checkIfLogged(){
        if(mAuth.getUid() == null){
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }
        else {
            CollectionReference collection = db.collection("user");
            Query query = collection.whereEqualTo("uid", mAuth.getUid()).limit(1);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            try {
                                name = document.get("first_name").toString();
                                name += " ";
                                name += document.get("last_name").toString().substring(0, 1);
                                name += ".";
                            } catch(Exception E) {
                                //TODO skip?
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        } else if (id == R.id.nav_account) {
            //Open account activity
            //startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        } else if (id == R.id.nav_add_classe) {
            //startActivity(new Intent(getApplicationContext(), AddClasses.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
