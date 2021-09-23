package com.cahyaa.chatapp.presentation.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cahyaa.chatapp.MainActivity;
import com.cahyaa.chatapp.data.membership.firebase.MembershipFirebaseImpl;
import com.cahyaa.chatapp.databinding.ActivitySignUpBinding;
import com.cahyaa.chatapp.presentation.login.LogInActivity;
import com.cahyaa.chatapp.utils.ValidateFormUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Boolean validateUsername, validateEmail, validatePassword, validateConfirm, isUsernameAvailable;
    private SignUpViewModel signUpViewModel;
    private MembershipFirebaseImpl repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        instanceViewModel();

        validateUsername = false;
        validateEmail = false;
        validatePassword = false;
        validateConfirm = false;
        isUsernameAvailable = false;

        instanceFirebase();

        //Mengirim data input sign up form
        binding.signupButtonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserProfile();
            }
        });

        //Mengarahkan ke LogInActivity ketika TextView "Masuk" diklik
        binding.signupTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LogInActivity.class);
                startActivity(intent);
            }
        });

        binding.signupTextInputUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //Mendeteksi perubahan setiap user mengetikkan input menggunakan TextWatcher
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String username = binding.signupTextInputUsername.getEditText().getText().toString().trim();

                //Regex Pattern untuk format username (kombinasi lowercase, angka, dan special characters)
                Pattern USERNAME_PATTERN = Pattern.compile("[a-z0-9\\_\\.]{0,20}");

                //Menampilkan warning message jika user mengirim input kosong & validasi format input
                ValidateFormUtils valid = new ValidateFormUtils();
                validateUsername = valid.validateField(username, binding.signupTextInputUsername, USERNAME_PATTERN.matcher(username).matches(), "Please fill the Username column.", "Username can only use lowercase, numbers, underscore, and dot.");

                validateForm();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.signupTextInputEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = binding.signupTextInputEmail.getEditText().getText().toString().trim();

                //Regex Pattern untuk format email (kombinasi uppercase, lowercase, angka, dan special characters dilanjut "@" lalu domain nya)
                Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");

                ValidateFormUtils valid = new ValidateFormUtils();
                validateEmail = valid.validateField(email, binding.signupTextInputEmail, EMAIL_ADDRESS_PATTERN.matcher(email).matches(), "Please fill the Email column.", "Wrong email format.");

                validateForm();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.signupTextInputPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = binding.signupTextInputPassword.getEditText().getText().toString().trim();
                String confirmPassword = binding.signupTextInputConfirm.getEditText().getText().toString();

                //Regex Pattern untuk format password (kombinasi uppercase, lowercase, angka, dan special characters)
                Pattern PASSWORD_PATTERN = Pattern.compile("[a-zA-Z0-9\\!\\@\\#\\$\\_\\.]{0,20}");

                //Panjang password hanya bisa sebanyak 6 s.d. 20 karakter
                boolean isLength = password.length() >= 6 || password.length() <= 20;

                ValidateFormUtils valid = new ValidateFormUtils();
                validatePassword = valid.validateField(password, binding.signupTextInputPassword, PASSWORD_PATTERN.matcher(password).matches() && isLength,
                        "Please fill the Password column.", "Must contain a - z, A - Z, !, @, #, $, _, .");

                validateForm();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.signupTextInputConfirm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = binding.signupTextInputPassword.getEditText().getText().toString();
                String confirmPassword = binding.signupTextInputConfirm.getEditText().getText().toString();

                ValidateFormUtils valid = new ValidateFormUtils();
                validateConfirm = valid.validateField(confirmPassword, binding.signupTextInputConfirm, confirmPassword.equals(password), "Please fill the Confirm Password column.", "Confirm Password didn't match.");

                validateForm();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void instanceViewModel() {
        repository = new MembershipFirebaseImpl();
        signUpViewModel = new SignUpViewModel(repository);
    }

    //Instance (koneksi) ke Firebase
    public void instanceFirebase() {
//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    //Membuat Button "Sign up" menjadi enabled hanya jika setiap kolom form telah terisi semuanya dengan format yang tepat
    public void validateForm() {
        String username = binding.signupTextInputUsername.getEditText().getText().toString();
        String email = binding.signupTextInputEmail.getEditText().getText().toString();
        String password = binding.signupTextInputPassword.getEditText().getText().toString();
        String confirmPassword = binding.signupTextInputConfirm.getEditText().getText().toString();

        binding.signupButtonSignup.setEnabled(!username.equals("") && !email.equals("") && !password.equals("") && !confirmPassword.equals("")
                && validateUsername && validateEmail && validatePassword && validateConfirm);
    }

    //Sinkronisasi (ambil) data user dari Firestore untuk cek ketersediaan Username
    private void getUserProfile() {
        String username = binding.signupTextInputUsername.getEditText().getText().toString();

        firebaseFirestore.collection("users").whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        isUsernameAvailable = queryDocumentSnapshots.isEmpty();

                        if (isUsernameAvailable) {
                            signUp();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Username has taken by another user. Please choose another one.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Failed to Sign up. " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Metode autentikasi melalui provider berupa email
    public void signUp() {
        String email = binding.signupTextInputEmail.getEditText().getText().toString();
        String password = binding.signupTextInputPassword.getEditText().getText().toString();

        signUpViewModel.signUp(email, password).observe(this, result -> {
            if (result.getLoading()) {
                binding.signupProgressBar.setVisibility(View.VISIBLE);
            } else {
                binding.signupProgressBar.setVisibility(View.GONE);
            }

            if (result.getData() != null) {
                saveDataUser();
            }

            if (result.getException() != null) {
                Toast.makeText(SignUpActivity.this, "Authentication failed. " + result.getException(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Koleksi input user yang berhasil membuat akun disimpan di Firestore Database
    public void saveDataUser() {
        Map<String, Object> user = new HashMap<>();
        //Add field
        user.put("username", binding.signupTextInputUsername.getEditText().getText().toString());
        user.put("email", binding.signupTextInputEmail.getEditText().getText().toString());
        user.put("password", binding.signupTextInputPassword.getEditText().getText().toString());

        //Start collection
        firebaseFirestore.collection("users")
                //Add document
                .add(user)
                //Jika data berhasil tersimpan
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(SignUpActivity.this, "Account Created.",
                                Toast.LENGTH_SHORT).show();
                        openMainPage();
                    }
                })
                //Jika data gagal tersimpan
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Firestore failed. " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Agar user tidak bisa kembali diarahkan ke SignUpActivity ketika tekan tombol Back setelah berhasil Sign up
    public void openMainPage() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}