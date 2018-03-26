package notegator.notegator;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomepageActivity extends AppCompatActivity {

    private FirebaseDatabase db;

    private LinkedHashMap<String, HeaderInfo> mySection = new LinkedHashMap<>();
    private ArrayList<HeaderInfo> SectionList = new ArrayList<>();

    private MyListAdapter listAdapter;
    private ExpandableListView expandableListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        db = FirebaseDatabase.getInstance();
        populate();

        //get reference to the ExpandableListView
        expandableListView = (ExpandableListView) findViewById(R.id.myList);

        //create the adapter by passing your ArrayList data
        listAdapter = new MyListAdapter(HomepageActivity.this, SectionList);

        //attach the adapter to the list
        expandableListView.setAdapter(listAdapter);

        //listener for child row click
        expandableListView.setOnChildClickListener(myListItemClicked);

        //listener for group heading click
        expandableListView.setOnGroupClickListener(myListGroupClicked);
        collapseAll();
    }

    //load some initial data into out list
    private void populate(){
        //TODO db entry here
        DatabaseReference ref = db.getReference("news");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey().toString();
                String text = dataSnapshot.getValue().toString();
                addProduct(key, text);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addProduct("CIS4914","Eddie Murphey added notes for February 28, 2018");
        addProduct("CIS4914","Johannes Kepler posted a response to your comment from January 1, 2018");
        addProduct("CIS4914","Bob added notes for Exam II");

        addProduct("COP4600","Odysseus added notes for February 26, 2018");
        addProduct("COP4600","Aeneas asked a question on February 2, 2018");
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

            startActivity(new Intent(getApplicationContext(), ClassActivity.class));
            return false;
        }
    };

    private int addProduct(String department, String product){

        int groupPosition = 0;

        //check the hash map if the group already exists
        HeaderInfo headerInfo = mySection.get(department);

        //add the group if doesn't exists
        if(headerInfo == null){
            headerInfo = new HeaderInfo();
            headerInfo.setName(department);
            mySection.put(department, headerInfo);
            SectionList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<DetailInfo> productList = headerInfo.getProductList();

        //size of the children list
        int listSize = productList.size();
        ++listSize;

        //create a new child and add that to the group
        DetailInfo detailInfo = new DetailInfo();
        detailInfo.setSequence(String.valueOf(listSize));
        detailInfo.setName(product);
        productList.add(detailInfo);
        headerInfo.setProductList(productList);

        //find the group position inside the list
        groupPosition = SectionList.indexOf(headerInfo);
        return groupPosition;
    }

}
