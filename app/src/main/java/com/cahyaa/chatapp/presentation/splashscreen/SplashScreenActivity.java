package com.cahyaa.chatapp.presentation.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.cahyaa.chatapp.MainActivity;
import com.cahyaa.chatapp.databinding.ActivitySplashScreenBinding;
import com.cahyaa.chatapp.presentation.onboarding.OnboardingActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private FirebaseAuth firebaseAuth;

    //Mengatur durasi splash screen selama 3 detik
    private static int SPLASH_SCREEN = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Menghilangkan teks pada area Status Bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        instanceFirebase();

        //Menjalankan activity setelah splash screen 3 detik berakhir
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Jika user berhasil Log in, maka OnboardingActivity tidak akan dimunculkan lagi
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                //Jika user tidak sedang Log in (telah melakukan clear data ataupun belum Sign up)
                } else {
                    startActivity(new Intent(getApplicationContext(), OnboardingActivity.class));
                }
                finish();
            }
        }, SPLASH_SCREEN);
    }

    //Instance (koneksi) ke Firebase
    public void instanceFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

}