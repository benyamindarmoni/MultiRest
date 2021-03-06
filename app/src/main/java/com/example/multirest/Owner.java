package com.example.multirest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.multirest.ui.Dish;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;
public class Owner extends AppCompatActivity {

    private SignInButton signIn;
    private  int RC_SIGN_IN=1;
    GoogleSignInClient mGoogleSignInClient;
    private  String TAG ="Owner";
    private FirebaseAuth mAuth;
    TextView s;
    private static ArrayList<String> owners = new ArrayList<String>();
    String id;
    EditText textid;
    private static  DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private static  DatabaseReference ownersRef = rootRef.child("owners");
    Button submitbuttom;


@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        signIn=(SignInButton)findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        textid=(EditText) findViewById(R.id.textid);
        submitbuttom=(Button) findViewById(R.id.textidok);
        s=(TextView)findViewById(R.id.textView13);

        submitbuttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id=textid.getText().toString();
                if(checkper()){
                    submitbuttom.setVisibility(View.GONE);
                    textid.setVisibility(View.GONE);
                    signIn.setVisibility(View.VISIBLE);
                }
                else{

                    Toast.makeText(Owner.this , "this is not a vaild number.", Toast.LENGTH_LONG).show();

                }

s.setText("לכניסה באמצעות גוגל לחץ כאן:");

            }
        });





        ///////////////////////////////
    ValueEventListener vel = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                String uid = ds.getKey();
                owners.add(uid);
                System.out.println(uid);
            }
            //Do what you need to do with your list



        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ownersRef.addListenerForSingleValueEvent(vel);
/////////////////////
    // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }
   public void open(){
       Intent intent=new Intent(this,OwnerOptions.class);
       startActivity(intent);
   }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if ( task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                           updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Owner.this , "you are not able to log in to google", Toast.LENGTH_LONG).show();

                        }

                        // ...
                    }
                });






    }

    private  boolean checkper() {//!!!!!!!!!!!!!NOT WORKING!!!!!!!!!!!!!
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
         return owners.contains(id);

    }


    private void updateUI(FirebaseUser user) {
        Intent intent=new Intent(this,OwnerOptions.class);
        startActivity(intent);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();


        Toast.makeText(this , "Name of the user : " + personName + " " + "user id :" + personId , Toast.LENGTH_SHORT).show();


        }



    }


}
