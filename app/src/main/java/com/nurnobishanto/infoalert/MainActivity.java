package com.nurnobishanto.infoalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.nurnobishanto.infoalert.Fragment.AboutFragment;
import com.nurnobishanto.infoalert.Fragment.HomeFragment;
import com.nurnobishanto.infoalert.Fragment.ProblemsListFragment;

public class MainActivity extends AppCompatActivity {

    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setTitle("Home");
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                new HomeFragment()).commit();

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    drawer.closeDrawer(GravityCompat.START);
                    getSupportActionBar().setTitle("Home");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                            new HomeFragment()).commit();
                    break;
                case R.id.nav_problem_list:
                    drawer.closeDrawer(GravityCompat.START);
                    getSupportActionBar().setTitle("Problems");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                            new ProblemsListFragment()).commit();
                    break;
                case R.id.nav_app_related:
                    startActivity(new Intent(this,AppRelatedActivity.class));
                    break;
                case R.id.nav_necessary:
                    startActivity(new Intent(this,NecessaryActivity.class));
                    break;
                case R.id.nav_about:
                    drawer.closeDrawer(GravityCompat.START);
                    getSupportActionBar().setTitle("About");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                            new AboutFragment()).commit();
                    break;
                case R.id.nav_rate_us:

                    try {
                        Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                        startActivity(marketIntent);
                    }catch(ActivityNotFoundException e) {
                        Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                        startActivity(marketIntent);
                    }
                    break;

            }
            return true;
        });
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        getSupportActionBar().setTitle("Home");
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                new HomeFragment()).commit();
        if (doubleBackToExitPressedOnce) {

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}