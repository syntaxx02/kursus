package com.cahyaa.chatapp.presentation.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.cahyaa.chatapp.R;
import com.cahyaa.chatapp.databinding.ActivityForgotPasswordBinding;
import com.cahyaa.chatapp.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Tombol "back" diatas kiri layar untuk kembali ke halaman sebelumnya
        binding.settingImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}