package com.example.sonu.location;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sonu on 10/29/2016.
 */
public class AddProblem extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseUser mFirebaseUser;

    EditText title;
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_problem);

        title=(EditText)findViewById(R.id.problem_title);
        content=(EditText)findViewById(R.id.problem_description);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        (findViewById(R.id.submit_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddProblem.this,"GETTING CURRENT LOCATION",Toast.LENGTH_SHORT).show();
                mGoogleApiClient.connect();
            }
        });

    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(AddProblem.this,"CONNECTED",Toast.LENGTH_SHORT).show();
            mFirebaseAuth = FirebaseAuth.getInstance();

            final String lattitude=(String.valueOf(mLastLocation.getLatitude()));
            final String longitude=(String.valueOf(mLastLocation.getLongitude()));

            mFirebaseAuth.signInAnonymously()
                    .addOnCompleteListener(AddProblem.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                        /*
                        Intent intent = new Intent(ProblemDetails.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        */
                                Toast.makeText(AddProblem.this,"successfull bitches",Toast.LENGTH_SHORT).show();

                                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mUserId = mFirebaseUser.getUid();
                                DatabaseReference xx=mDatabase.push();
                                xx.child("lattitude").setValue(lattitude);
                                xx.child("longitude").setValue(longitude);
                                xx.child("title").setValue(title.getText().toString());
                                xx.child("content").setValue(content.getText().toString());
                                AddProblem.this.finish();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddProblem.this);
                                builder.setMessage(task.getException().getMessage())
                                        .setTitle("ERROR BITCHES")
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }

                    });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
