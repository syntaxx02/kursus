package com.cahyaa.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cahyaa.chatapp.databinding.ActivityMainBinding;
import com.cahyaa.chatapp.presentation.profile.ProfileFragment;
import com.cahyaa.chatapp.presentation.setting.SettingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.mainToolbar);

        setBotNavBar();
        initView();
    }

    //Menampilkan setiap halaman fragment sesuai dengan ikon Bottom Navigation Bar yang diklik
    private void setBotNavBar() {
        binding.botnavbarFragments.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.menu_page1) {
                    selectedFragment = new ProfileFragment();
                } else if (item.getItemId() == R.id.menu_page2) {
                    selectedFragment = new ProfileFragment();
                } else if (item.getItemId() == R.id.menu_page3) {
                    selectedFragment = new ProfileFragment();
                } else if (item.getItemId() == R.id.menu_page4) {
                    selectedFragment = new ProfileFragment();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.botnavbar_framelayout, selectedFragment).commit();

                return true;
            }
        });
    }

    //Menentukan halaman fragment yang dimuat paling awal setelah user masuk ke aplikasi
    private void initView() {
        getSupportFragmentManager().beginTransaction().replace(R.id.botnavbar_framelayout, new ProfileFragment()).commit();
    }

    //Menampilkan Elemen Kebab Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Menampilkan 2 Options setelah Kebab Menu diklik
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                Intent intent = new Intent(getBaseContext(), SettingActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_about:
                Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}