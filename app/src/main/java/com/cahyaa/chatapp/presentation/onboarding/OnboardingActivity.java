package com.cahyaa.chatapp.presentation.onboarding;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.cahyaa.chatapp.databinding.ActivityOnboardingBinding;
import com.cahyaa.chatapp.presentation.signup.SignUpActivity;

public class OnboardingActivity extends AppCompatActivity implements OnboardingListener {

    private OnboardingAdapter onboardingAdapter;
    private ActivityOnboardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        onboardingAdapter = new OnboardingAdapter(this, this);
        binding.onboardingViewPager.setAdapter(onboardingAdapter);

        transparentStatusBar();
    }

    //Listener ketika Button "Lanjut" diklik
    @Override
    public void onContinueClickListener() {
        Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
        startActivity(intent);
    }

    //Listener ketika TextView "Lewati" diklik
    @Override
    public void onSkipClickListener() {
        Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
        startActivity(intent);
    }

    //Membuat background Status Bar menjadi transparan
    private void transparentStatusBar() {

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}