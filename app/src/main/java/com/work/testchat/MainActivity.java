package com.work.testchat;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.work.testchat.databinding.ActivityMainBinding;
import com.work.testchat.interfaces.AccountChanges;
import com.work.testchat.localDb.ApplicationData;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements AccountChanges {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    Realm realm;
    ApplicationData applicationData;
    TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        realm = Realm.getDefaultInstance();
        applicationData = realm.where(ApplicationData.class).findFirst();

        DrawerLayout drawer = binding.drawerLayout;

        setSupportActionBar(binding.appBarMain.toolbar);
        NavigationView navigationView = binding.navView;
        userNameTextView = navigationView.getHeaderView(0).findViewById(R.id.draw_layout_user_name);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_account,R.id.nav_chats)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        GlobalObjects.socket.close();
        super.onDestroy();
    }

    @Override
    public void onAccountChanged() {
        if (GlobalObjects.user != null) {
            userNameTextView.setText(GlobalObjects.user.userName);
        } else {
            userNameTextView.setText(R.string.authentication_required_short);
        }

    }
}