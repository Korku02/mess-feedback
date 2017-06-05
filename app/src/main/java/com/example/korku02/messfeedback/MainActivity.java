package com.example.korku02.messfeedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;


    //defining a database reference
    private DatabaseReference databaseReference;

    //our new views
    private TextView editTextUserName, editTextUserHostel;

    private Button buttonScanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();


        //if the user is not logged in
        //that means current user will return null
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, login.class));
        }


        FirebaseUser user = firebaseAuth.getCurrentUser();

        String uid = user.getUid();

        //getting the database reference
        databaseReference = FirebaseDatabase.getInstance().getReference(uid);

        //getting the views from xml resource
        editTextUserHostel = (TextView) findViewById(R.id.editTextUserHostel);
        editTextUserName = (TextView) findViewById(R.id.editTextUserName);

        buttonScanner = (Button) findViewById(R.id.buttonScanner);


        //initializing views
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);


        //adding listener to button
        buttonLogout.setOnClickListener(this);

        buttonScanner.setOnClickListener(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_feedback) {
            startActivity(new Intent(this, scanner.class));

        } else if (id == R.id.nav_logout) {
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, login.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Getting the data from snapshot
                UserInformation person = dataSnapshot.getValue(UserInformation.class);




                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Adding it to a string
                String userName = person.getName();
                if(userName == null){
                    userName = "userName";
                }
                String userHostel = person.getHostel();

                if(userHostel == null){
                    userHostel = "userHostel";
                }
                //displaying logged in user name
                textViewUserEmail.setText("Welcome " + person.getName());

                //Displaying it on textview
                editTextUserName.setText("Name: "+ userName);
                editTextUserHostel.setText("Hostel: "+ userHostel);
                //textViewUserName.setText(userName);
                //textViewUserHostel.setText(userHostel);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());


            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == buttonLogout) {
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, login.class));
        }


        if (v == buttonScanner) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Getting the data from snapshot
                    UserInformation person = dataSnapshot.getValue(UserInformation.class);
                    String username = person.getName();
                    String hostel = person.getHostel();

                    Intent intent = new Intent(getApplicationContext(), scanner.class);
                    intent.putExtra("username", username);
                    intent.putExtra("hostel", hostel);
                    startActivity(intent);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getMessage());


                }
            });
        }


    }
}

