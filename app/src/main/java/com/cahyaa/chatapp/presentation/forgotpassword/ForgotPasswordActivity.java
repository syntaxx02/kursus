package com.cahyaa.chatapp.presentation.forgotpassword;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cahyaa.chatapp.databinding.ActivityForgotPasswordBinding;
import com.cahyaa.chatapp.utils.ValidateFormUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth firebaseAuth;
    private Boolean validateEmail, validateNewPassword, validateConfirmNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        validateEmail = false;
        validateNewPassword = false;
        validateConfirmNew = false;

        instanceFirebase();

        //Tombol "back" diatas kiri layar untuk kembali ke halaman sebelumnya
        binding.forgotImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.forgotTextInputEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = binding.forgotTextInputEmail.getEditText().getText().toString().trim();

                //Regex Pattern untuk format email (kombinasi uppercase, lowercase, angka, dan special characters dilanjut "@" lalu domain nya)
                Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");

                //Menampilkan warning message jika user mengirim input kosong & validasi format input
                ValidateFormUtils valid = new ValidateFormUtils();
                validateEmail = valid.validateField(email, binding.forgotTextInputEmail, EMAIL_ADDRESS_PATTERN.matcher(email).matches(), "Please fill the Email column.", "Wrong format email");

                validateForm();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.forgotButtonForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    //Instance (koneksi) ke Firebase
    public void instanceFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Untuk kirim ke email
    public void resetPassword() {
        firebaseAuth.sendPasswordResetEmail(binding.forgotTextInputEmail.getEditText().getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ForgotPasswordActivity.this, "Reset Password request has been sent to your email.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to sent Reset Password request. " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Membuat Button "Sign up" menjadi enabled hanya jika setiap kolom form telah terisi semuanya dengan format yang tepat
    public void validateForm() {
        String email = binding.forgotTextInputEmail.getEditText().getText().toString();

        binding.forgotButtonForgot.setEnabled(!email.equals("") && validateEmail);
    }

}