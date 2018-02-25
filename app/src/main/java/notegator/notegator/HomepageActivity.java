package notegator.notegator;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class HomepageActivity extends AppCompatActivity implements OnClickListener {

    private LinkedHashMap<String, HeaderInfo> mySection = new LinkedHashMap<>();
    private ArrayList<HeaderInfo> SectionList = new ArrayList<>();

    private MyListAdapter listAdapter;
    private ExpandableListView expandableListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //Just add some data to start with
        AddProduct();

        //get reference to the ExpandableListView
        expandableListView = (ExpandableListView) findViewById(R.id.myList);
        //create the adapter by passing your ArrayList data
        listAdapter = new MyListAdapter(HomepageActivity.this, SectionList);
        //attach the adapter to the list
        expandableListView.setAdapter(listAdapter);

        //expand all Groups
        expandAll();

        //listener for child row click
        expandableListView.setOnChildClickListener(myListItemClicked);
        //listener for group heading click
        expandableListView.setOnGroupClickListener(myListGroupClicked);
        collapseAll();
    }

    public void onClick(View v) {
/*
        switch (v.getId()) {

            //add entry to the List
            case R.id.add:

                Spinner spinner = (Spinner) findViewById(R.id.department);
                String department = spinner.getSelectedItem().toString();
                EditText editText = (EditText) findViewById(R.id.product);
                String product = editText.getText().toString();
                editText.setText("");

                //add a new item to the list
                int groupPosition = addProduct(department,product);
                //notify the list so that changes can take effect
                listAdapter.notifyDataSetChanged();

                //collapse all groups
                collapseAll();
                //expand the group where item was just added
                expandableListView.expandGroup(groupPosition);
                //set the current group to be selected so that it becomes visible
                expandableListView.setSelectedGroup(groupPosition);

                break;
        }
        */
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

    //load some initial data into out list
    private void AddProduct(){

        addProduct("Vegetable","Potato");
        addProduct("Vegetable","Cabbage");
        addProduct("Vegetable","Onion");

        addProduct("Fruits","Apple");
        addProduct("Fruits","Orange");
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
            Toast.makeText(getBaseContext(), "Clicked on Detail " + headerInfo.getName()
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
            Toast.makeText(getBaseContext(), "Child on Header " + headerInfo.getName(),
                    Toast.LENGTH_LONG).show();

            return false;
        }
    };

    //here we maintain our products in various departments
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
        //add to the counter
        listSize++;

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
