package notegator.notegator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        //checks if user is already signed in
        if(mAuth.getCurrentUser() != null){
            //startActivity(new Intent(getApplicationContext(), .class));
            //finish();
        }
        addButtons();
    }

    private void addButtons() {
        final Button facebookLogin = findViewById(R.id.btn_facebook);
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin();
            }
        });

        final Button googleLogin = findViewById(R.id.btn_google);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin();
            }
        });

        final Button emailLogin = findViewById(R.id.btn_email);
        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailLogin();
            }
        });

        final TextView emailSignUp = findViewById(R.id.register_email_text);
        emailSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailSignUp();
            }
        });
    }

    private void facebookLogin() {
        emailLogin();
    }

    private void googleLogin() {
        emailLogin();
    }

    private void emailLogin() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private void emailSignUp() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        isLogged();
    }

    private void isLogged() {
        // Check if user is signed in
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // update buttons and stuff here with user info
            //mEmailTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            //mUidTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            //go to home screen
            //startActivity(new Intent(getApplicationContext(), Home.class));
            //finish();

            //findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
            //findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        }
        /*else {
            // update buttons as if user is signed out
            mEmailTextView.setText(R.string.signed_out);
            mUidTextView.setText(null);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
        }*/
    }
    public void onClick(View v) {
        /*
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.email_sign_in_button) {
            signInEmail();
        }*/
    }
}

