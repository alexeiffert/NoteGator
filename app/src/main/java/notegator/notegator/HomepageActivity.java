package notegator.notegator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle AB_toggle;
    private SwipeRefreshLayout refreshHome;
    private LinkedHashMap<String, HeaderInfo> mySection = new LinkedHashMap<>();
    private ArrayList<HeaderInfo> SectionList = new ArrayList<>();
    private NewsListAdapter listAdapter;
    private ExpandableListView expandableListView;

    private ArrayList<String> userClasses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        mAuth = FirebaseAuth.getInstance();
        checkIfLogged();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        drawerLayout = findViewById(R.id.drawerLayout);
        AB_toggle = new
                ActionBarDrawerToggle(this, drawerLayout,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(AB_toggle);
        AB_toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configureSwipeRefresh();
        getUserClasses();  // Asynchronous callback to populateList() and configureList()
    }

    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            expandableListView.expandGroup(i);
        }
    }

    //method to collapse all groups
    private void collapseAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            expandableListView.collapseGroup(i);
        }
    }

    //our child listener
    private OnChildClickListener myListItemClicked =  new OnChildClickListener() {

        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {
            //get the group header
            HeaderInfo headerInfo = SectionList.get(groupPosition);
            //get the child info
            DetailInfo detailInfo =  headerInfo.getProductList().get(childPosition);

            if(!detailInfo.getThumbnail().equals("")) {
                storage.getReferenceFromUrl(detailInfo.getThumbnail()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri downloadUrl)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(downloadUrl.toString()));
                        startActivity(intent);
                        finish();
                    }
                });
            }
            return false;
        }
    };

    //our group listener
    private OnGroupClickListener myListGroupClicked =  new OnGroupClickListener() {

        public boolean onGroupClick(ExpandableListView parent, View v,
                                    int groupPosition, long id) {
            //get the group header
            HeaderInfo headerInfo = SectionList.get(groupPosition);
            if(parent.isGroupExpanded(groupPosition)) {
                Context context = getApplicationContext();
                Intent intent = new Intent(context, ClassActivity.class);
                intent.putExtra("courseNumber", headerInfo.getName());
                context.startActivity(intent);
                finish();
            }
            return false;
        }
    };

    private int addNews(String date, String className, String text, String path, String thumbnail){

        int groupPosition = 0;

        //check the hash map if the group already exists
        HeaderInfo headerInfo = mySection.get(className);

        //Add the group if it doesn't exist
        if(headerInfo == null){
            headerInfo = new HeaderInfo(className);
            mySection.put(className, headerInfo);
            SectionList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<DetailInfo> classList = headerInfo.getProductList();

        //size of the children list
        int listSize = classList.size();
        ++listSize;

        //create a new child and add that to the group
        String sequence = String.valueOf(listSize);
        DetailInfo detailInfo = new DetailInfo(sequence, text, date, path, thumbnail);
        classList.add(detailInfo);
        headerInfo.setProductList(classList);

        //find the group position inside the list
        groupPosition = SectionList.indexOf(headerInfo);
        return groupPosition;
    }

    private void getUserClasses(){
        String uid = mAuth.getUid();
        CollectionReference collectionReference = db.collection("user");
        Query query = collectionReference.whereEqualTo("uid", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document : task.getResult()) {
                        userClasses = (ArrayList<String>)document.get("classes");
                    }
                    //Callback
                    populateList();
                }
            }
        });
    }

    //Helper methods for Expandable List
    private void populateList(){
        CollectionReference collectionReference = db.collection("notes");
        for(final String className : userClasses) {
            //TODO trouble with ordering by time
            Query query = collectionReference.whereEqualTo("courseNumber", className).limit(20);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            try {
                                String date = document.get("date").toString();
                                String text = document.get("description").toString();
                                String thumbnail = "";
                                if(document.get("thumbnail") != null)
                                    thumbnail = document.get("thumbnail").toString();
                                String path = document.get("path").toString();
                                addNews(date, className, text, path, thumbnail);

                            } catch(Exception E) {
                                Log.d("", "Problem adding notes");
                            }
                        }
                        listAdapter.notifyDataSetChanged();
                    }
                    checkEmpty(className);
                }
            });
        }
        configureList();
    }

    private void checkEmpty(String className){
        if(mySection.get(className) == null){  // If the group doesn't have any notes yet
            addNews("Nothing yet", className,
                    "The designated notetaker for this class hasn't " +
                            "posted any notes. Check back soon!",
                    "", "");
        }
    }

    private void configureList(){
        //get reference to the ExpandableListView
        expandableListView = (ExpandableListView) findViewById(R.id.myList);

        //create the adapter by passing your ArrayList data
        listAdapter = new NewsListAdapter(HomepageActivity.this, SectionList);

        //attach the adapter to the list
        expandableListView.setAdapter(listAdapter);

        //listener for child row click
        expandableListView.setOnChildClickListener(myListItemClicked);

        //listener for group heading click
        expandableListView.setOnGroupClickListener(myListGroupClicked);
        collapseAll();
    }

    private void configureSwipeRefresh(){
        refreshHome = findViewById(R.id.refreshHome);
        refreshHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO refresh isn't working correctly
                //getUserClasses();
                mySection.clear();
                SectionList.clear();
                getUserClasses();
                refreshHome.setRefreshing(false); //stop refresh animation when done;
            }
        });
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
            startActivity(new Intent(getApplicationContext(), ClassRecyclerView.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return AB_toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void checkIfLogged(){
        if(mAuth.getUid() == null){
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }
    }
}
