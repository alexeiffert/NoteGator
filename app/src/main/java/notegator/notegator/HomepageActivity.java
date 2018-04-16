package notegator.notegator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class HomepageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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
        db = FirebaseFirestore.getInstance();
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

            //display it or do something with it
            Toast.makeText(getBaseContext(), "You're looking at " + headerInfo.getName()
                           + "/" + detailInfo.getName(), Toast.LENGTH_LONG).show();

            startActivity(new Intent(getApplicationContext(), ClassActivity.class));
            return false;
        }
    };

    //our group listener
    private OnGroupClickListener myListGroupClicked =  new OnGroupClickListener() {

        public boolean onGroupClick(ExpandableListView parent, View v,
                                    int groupPosition, long id) {
            //get the group header
            HeaderInfo headerInfo = SectionList.get(groupPosition);

            //display it or do something with it
            Toast.makeText(getBaseContext(), "You're looking at " + headerInfo.getName(),
                           Toast.LENGTH_LONG).show();

            return false;
        }
    };

    private int addNews(String date, String className, String text, String thumbnail){

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
        DetailInfo detailInfo = new DetailInfo(sequence, text, date, thumbnail);
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
        CollectionReference collectionReference = db.collection("news");
        for(String className : userClasses) {
            //TODO trouble with ordering by time
            Query query = collectionReference.whereEqualTo("class", className).limit(20);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String date = document.get("time").toString();
                            String key = document.get("class").toString();
                            String text = document.get("text").toString();
                            String thumbnail = document.get("thumbnail").toString();
                            addNews(date, key, text, thumbnail);
                        }
                        listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        configureList();
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
}
