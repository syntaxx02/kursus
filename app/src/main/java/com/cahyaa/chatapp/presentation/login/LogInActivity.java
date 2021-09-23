package com.cahyaa.chatapp.presentation.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cahyaa.chatapp.MainActivity;
import com.cahyaa.chatapp.databinding.ActivityLogInBinding;
import com.cahyaa.chatapp.presentation.forgotpassword.ForgotPasswordActivity;
import com.cahyaa.chatapp.presentation.signup.SignUpActivity;
import com.cahyaa.chatapp.utils.ValidateFormUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class LogInActivity extends AppCompatActivity {

    private ActivityLogInBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Boolean validateEmail, validatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        validateEmail = false;
        validatePassword = false;

        instanceFirebase();

        //Mengarahkan ke ForgotPasswordActivity ketika TextView "Lupa Password" diklik
        binding.loginTextViewLupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        //Mengirim data input log in form
        binding.loginButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        //Mengarahkan ke SignUpActivity ketika TextView "Daftar" diklik
        binding.loginTextViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        binding.loginTextInputEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = binding.loginTextInputEmail.getEditText().getText().toString().trim();

                //Regex Pattern untuk format email (kombinasi uppercase, lowercase, angka, dan special characters dilanjut "@" lalu domain nya)
                Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");

                //Menampilkan warning message jika user mengirim input kosong & validasi format input
                ValidateFormUtils valid = new ValidateFormUtils();
                validateEmail = valid.validateField(email, binding.loginTextInputEmail, EMAIL_ADDRESS_PATTERN.matcher(email).matches(), "Please fill the Email column.", "Wrong email format.");

                validateForm();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.loginTextInputPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = binding.loginTextInputPassword.getEditText().getText().toString().trim();

                //Regex Pattern untuk format password (kombinasi uppercase, lowercase, angka, dan special characters)
                Pattern PASSWORD_PATTERN = Pattern.compile("[a-zA-Z0-9\\!\\@\\#\\$\\_\\.]{0,20}");

                //Panjang password hanya bisa sebanyak 6 s.d. 20 karakter
                boolean isLength = password.length() >= 6 || password.length() <= 20;

                ValidateFormUtils valid = new ValidateFormUtils();
                validatePassword = valid.validateField(password, binding.loginTextInputPassword, PASSWORD_PATTERN.matcher(password).matches() && isLength,
                        "Please fill the Password column.", "Must contain a - z, A - Z, !, @, #, $, _, .");

                validateForm();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //Instance (koneksi) ke Firebase
    public void instanceFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    //Membuat Button "Log in" menjadi enabled hanya jika setiap kolom form telah terisi semuanya dengan format yang tepat
    public void validateForm() {
        String email = binding.loginTextInputEmail.getEditText().getText().toString();
        String password = binding.loginTextInputPassword.getEditText().getText().toString();

        binding.loginButtonLogin.setEnabled(!email.equals("") && !password.equals("")
                && validateEmail && validatePassword);
    }

    //Metode autentikasi melalui provider berupa email
    public void logIn() {
        String email = binding.loginTextInputEmail.getEditText().getText().toString();
        String password = binding.loginTextInputPassword.getEditText().getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Log in success, update UI with the signed-in user's information
                            Toast.makeText(LogInActivity.this, "You are logged in.",
                                    Toast.LENGTH_SHORT).show();
                            openMainPage();
                        } else {
                            // If log in fails, display a message to the user.
                            String error = task.getException().getMessage();
                            Toast.makeText(LogInActivity.this, "Failed to Log in. " + error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Agar user tidak bisa kembali diarahkan ke LogInActivity ketika tekan tombol Back setelah berhasil Log in
    public void openMainPage() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}