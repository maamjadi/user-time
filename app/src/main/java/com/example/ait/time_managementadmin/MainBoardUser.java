package com.example.ait.time_managementadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MainBoardUser extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    private static final String TAG = "EmailPassword";

    private EditText name;
    private Toolbar toolbar;
    private ArrayList<Integer> years;
    private ArrayList<Integer> months;
    private ArrayList<Integer> days;
    private String fullDate;
    private String event;
    private String eventDetail;
    private String eventTitle;
    private Integer counter = 0;
    final ArrayList<String> events = new ArrayList<>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_board_user);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        ref = firebaseDatabase.getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        ref = firebaseDatabase.getReference("Users");
        ref = databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Events");



        years = new ArrayList<>();
        months = new ArrayList<>();
        days = new ArrayList<>();
        final ExpandableStickyListHeadersListView expandableStickyList = (ExpandableStickyListHeadersListView) findViewById(R.id.list);
        StickyListHeadersAdapter adapter = new MyAdapter(this, events);

        expandableStickyList.setAdapter(adapter);
        expandableStickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainBoardUser.this, events.get(position).split("_")[1], Toast.LENGTH_LONG).show();
            }
        });

        expandableStickyList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (expandableStickyList.isHeaderCollapsed(headerId)) {
                    expandableStickyList.expand(headerId);
                } else {
                    expandableStickyList.collapse(headerId);
                }
            }
        });


        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    ref = firebaseDatabase.getReference(user.getUid());
                    ref = firebaseDatabase.getReference("Events");


                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // finish();
                    Intent back = new Intent(MainBoardUser.this, LoginActivity.class);
                    startActivity(back);
                }
            }
        };
//        if (firebaseUser != null) {
//            firebaseUser = firebaseAuth.getCurrentUser();
//
//        }

        //iterateInSnapshotYears();

//        iterateInSnapshotMonths(String.valueOf(years.get(0)));
//        iterateInSnapshotDays();


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> objectEventDate = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String date : objectEventDate.keySet()) {

                    String tempDate = date;

                    // ref = ref.child(date);
                    DataSnapshot tempSnapShot = dataSnapshot.child(date);

                    Map<String, Object> objectEvent = (HashMap<String, Object>) tempSnapShot.getValue();
                    if (objectEvent != null) {
                        for (String time : objectEvent.keySet()) {
                            //eventTitle = objectEvent.get(time).toString().split("_")[0];
                            event = date + "-" + time + " : " + objectEvent.get(time);
                            events.add(event);

                        }
                    }


                    events.add(" - _ ");

//                    ref.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Map<String, Object> objectEvent = (HashMap<String, Object>) dataSnapshot.getValue();
//                            if (objectEvent != null) {
//                                for (String time : objectEvent.keySet()) {
//                                    eventTitle = objectEvent.get(time).toString().split("_")[0];
//                                    event = fullDate + "-" + time + " : " + objectEvent.get(time);
//                                    events.add(event);
//
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Do you want to add a new event?", Snackbar.LENGTH_LONG)
                //   .setAction("Add", null).show();
                // finish();
                Intent addEvent = new Intent(MainBoardUser.this, Addevent.class);
                startActivity(addEvent);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        getMenuInflater().inflate(R.menu.main_board_user, menu);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.Sign_out) {
            firebaseAuth.signOut();
            Intent back = new Intent(MainBoardUser.this, LoginActivity.class);
            startActivity(back);
            //  finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MainBoardUser Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private void iterateInSnapshotYears() {

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> objectYear = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String year : objectYear.keySet()) {
                    years.add(Integer.parseInt(year));
                    iterateInSnapshotMonths(year);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void iterateInSnapshotMonths(final String year) {
        ref = ref.child(year);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> objectMonth = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String month : objectMonth.keySet()) {
                    months.add(Integer.parseInt(month));
                    iterateInSnapshotDays(month);
                }
                months = months;
                days = days;
                years = years;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void iterateInSnapshotDays(String month) {
        ref = ref.child(month);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // DataSnapshot tempsnap = dataSnapshot;
                //Integer howMany = numberOfChildern(tempsnap);
                // if (howMany > 1) {
                // ArrayList<Map<String, Object>> objectDay = (ArrayList<Map<String, Object>>) dataSnapshot.getValue();
                //days = getIndexes(objectDay);
                // for (String day : objectDay.get(2).keySet()) {

                //  }
                // } //else if (howMany == 1) {
                Map<String, Object> objectDay = (HashMap<String, Object>) dataSnapshot.getValue();
                for (String day : objectDay.keySet()) {
                    days.add(Integer.parseInt(day));

                    //}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Integer> getIndexes(ArrayList<Map<String, Object>> arrayList) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (Integer index = 0; index < arrayList.size(); index++) {

            if (arrayList.get(index) != null) {
                indexes.add(index);
            }

        }
        return indexes;

    }

    private Integer numberOfChildern(DataSnapshot snapshot) {
        Integer counter = 0;
        for (DataSnapshot child : snapshot.getChildren()) {
            counter++;
        }

        return counter;
    }
    private void cleanEvents() {
        ArrayList<String> tempEvents = new ArrayList<String>();
        for (int i = 0, j = 1; i < events.size(); ++i, ++j) {

            if (Integer.parseInt(events.get(j).split("-")[0]) < Integer.parseInt(events.get(i).split("-")[0])) {
                String temp = events.get(i);
                events.add(i, events.get(j));
                events.add(j, temp);
            }
        }




}

}
