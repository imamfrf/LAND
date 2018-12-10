package com.papb.imamfrf.land;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mListener;
    public NavigationView drawNav;
    public BottomNavigationView btmNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                } else {
                    Toast.makeText(getApplicationContext(), "Keluar ", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }


            }
        };


        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        //btm nav
        btmNav = findViewById(R.id.navigation);
        btmNav.setOnNavigationItemSelectedListener(this);

        //draw nav
        drawNav = findViewById(R.id.nav_view);
        drawNav.setNavigationItemSelectedListener(this);
        loadFragment(new PromoFragment());



        mDatabase.child(auth.getCurrentUser().getUid()).child("nama").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView navEmail = (TextView) headerView.findViewById(R.id.tv_email);
                navEmail.setText(auth.getCurrentUser().getEmail());
                TextView navUser = (TextView) headerView.findViewById(R.id.tv_user);
                navUser.setText(dataSnapshot.getValue(String.class));
                //Toast.makeText(MainActivity.this, dataSnapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        mDrawer = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawer,R.string.open,R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean loadFragment(android.support.v4.app.Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
//        else if (fragment.equals(new SettingsFragment())){
//            getSupportFragmentManager().beginTransaction().replace(R.id.drawer, fragment).commit();
//            return true;
//
//        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        android.support.v4.app.Fragment fragment = null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()){
            case R.id.navigation_promo:
                fragment = new PromoFragment();
                loadFragment(fragment);
                ft.remove(new PromoFragment());
                ft.remove(new JadwalFragment());
                ft.remove(new SettingsFragment());
                ft.commit();
            break;
            case R.id.navigation_jadwal:
                fragment = new JadwalFragment();
                loadFragment(fragment);
      //          drawNav.getMenu().getItem(1).setChecked(true);
                ft.remove(new PromoFragment());
                ft.remove(new AktivitasFragment());
                ft.remove(new SettingsFragment());
                ft.commit();
                break;
            case R.id.navigation_aktivitas:
                fragment = new AktivitasFragment();
                loadFragment(fragment);
        //        drawNav.getMenu().getItem(1).setChecked(true);
                ft.remove(new PromoFragment());
                ft.remove(new JadwalFragment());
                ft.remove(new SettingsFragment());
                ft.commit();
                break;
            case R.id.nav_user_info:
                startActivity(new Intent(MainActivity.this, UserInfo.class));
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_logout:
                auth.signOut();
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                loadFragment(fragment);
               // drawNav.getMenu().getItem().setChecked(true);
                ft.remove(new PromoFragment());
                ft.remove(new JadwalFragment());
                ft.remove(new AktivitasFragment());
                ft.commit();

                for (int i = 0; i < btmNav.getMenu().size(); i++){
                    btmNav.getMenu().getItem(i).setChecked(true);
                }
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //getSupportActionBar().setTitle("Lamaran Masuk");
        auth.addAuthStateListener(mListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mListener != null){

            auth.removeAuthStateListener(mListener);
        }
    }

}
