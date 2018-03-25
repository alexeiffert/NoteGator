package notegator.notegator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //login(emailStr);
    }

    private void login(String emailStr) {
        //MongoCollection<Document> collection = db.getCollection("users");
        //Document userDocument = collection.find(Filters.eq("email", emailStr)).first();
        //System.out.println(userDocument.toJson());
    }
}
