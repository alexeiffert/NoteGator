package notegator.notegator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.BsonReader;
import org.bson.Document;

public class SignInActivity extends AppCompatActivity {

    private MongoDatabase db;
    private BsonReader br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        MongoClientURI mongoClientURI = new MongoClientURI(
                "mongodb://aeiffert1:furniture1@ds031915.mlab.com:31915/notegator");
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        db = mongoClient.getDatabase("notegator");

        String emailStr = "abby.hulshult@gmail.com";
        login(emailStr);
    }

    private void login(String emailStr) {
        MongoCollection<Document> collection = db.getCollection("users");
        Document userDocument = collection.find(Filters.eq("email", emailStr)).first();
        System.out.println(userDocument.toJson());
    }
}
