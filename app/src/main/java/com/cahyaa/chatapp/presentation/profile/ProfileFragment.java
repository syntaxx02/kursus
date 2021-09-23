package com.cahyaa.chatapp.presentation.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cahyaa.chatapp.R;
import com.cahyaa.chatapp.data.membership.model.User;
import com.cahyaa.chatapp.databinding.FragmentProfileBinding;
import com.cahyaa.chatapp.presentation.forgotpassword.ForgotPasswordActivity;
import com.cahyaa.chatapp.presentation.login.LogInActivity;
import com.cahyaa.chatapp.presentation.signup.SignUpActivity;
import com.cahyaa.chatapp.utils.ValidateFormUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    public Uri imageUri;

    FirebaseUser currentUser;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Instance (koneksi) ke Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        email = currentUser.getEmail();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        getUserProfile();

        //Menampilkan hasil upload foto profil user
        binding.profileImageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfilePicture();
            }
        });

        //Menambahkan akun baru (mengarahkan ke SignUpActivity) ketika TextView "Tambahkan Akun Baru" diklik
        binding.profileLinearLayoutAddNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        //Mengarahkan ke LogInActivity ketika TextView "Log out" diklik
        binding.profileLinearLayoutLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertLogOutDialog();
            }
        });

        //Mengarahkan ke LogInActivity ketika TextView "Hapus Akun" diklik
        binding.profileLinearLayoutDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertHapusAkunDialog();
            }
        });

        //Mengisi ... pada text di edit_profile_textView_judul (menyesuaikan dengan ikon pena yang diklik) menjadi Username
        binding.profileImageViewEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Username", "username", "Username updated.", "Failed to update username. ");
            }
        });

        //Mengisi ... pada text di edit_profile_textView_judul (menyesuaikan dengan ikon pena yang diklik) menjadi Status
        binding.profileImageViewEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Status", "status", "Status updated.", "Failed to update status. ");
            }
        });

        //Mengarahkan ke ForgotPasswordActivity ketika ikon pena edit Password diklik
        binding.profileImageViewEdit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    //Sinkronisasi (ambil) data user dari Firestore
    private void getUserProfile() {
        firebaseFirestore.collection("users").whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        User currentUser = documentSnapshot.toObject(User.class);

                        binding.profileTextViewUsernameResult.setText(currentUser.getUsername());
                        binding.profileTextViewStatusResult.setText(currentUser.getStatus());
                        binding.profileTextViewEmailResult.setText(currentUser.getEmail());
                        if (isDetached() == false) {
                            RequestOptions requestOptions = new RequestOptions();
                            //Menampilkan foto profil berupa semacam avatar default (jika user belum upload foto profilnya)
                            requestOptions.placeholder(R.drawable.user);
                            //Jika URL gagal ter-load
                            requestOptions.error(R.drawable.user);
                            Glide.with(requireContext()).load(currentUser.getPictureURL()).apply(requestOptions).into(binding.profileImageViewProfilePicture);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    //Membuka semacam galeri (penyimpanan bersama internal) untuk memilih foto profil
    private void chooseProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.profileImageViewProfilePicture.setImageURI(imageUri);
            uploadProfilePicture();
        }
    }

    private void uploadProfilePicture() {
        //Menampilkan ProgressDialog saat proses upload foto profil
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Mengunggah Foto Profil..");
        progressDialog.show();

        // Create a reference
        final String randomKey = UUID.randomUUID().toString();
        StorageReference fileReference = storageReference.child("images/" + randomKey);

        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Menyimpan uniform resource locator (URL) dari setiap foto profil yang berhasil diupload
                        fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String imageLink = task.getResult().toString();
                                editUserData("pictureURL", imageLink, "Profile picture updated.", "Failed to update profile picture. ");
                                progressDialog.dismiss();
                                Toast.makeText(requireActivity(), "Profile picture uploaded.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(requireActivity(), "Failed to upload profile picture. " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                //Menampilkan perhitungan progres upload dalam % pada ProgressDialog
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progressPercent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Mengunggah: " + (int) progressPercent + "%");
                        //Mencegah agar AlertDialog tidak tertutup jika user klik area diluar AlertDialog maupun melalui klik / swipe tombol back pada navigation gesture di smartphone
                        progressDialog.setCancelable(false);
                    }
                });
    }

    //Menampilkan BottomSheetDialog jika user klik setiap ikon pena
    public void showEditDialog(String field1, String saveField, String toastSuccessMessages, String toastFailedMessages) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.layout_edit_profile_dialog);

        TextView editProfileTextViewJudul = bottomSheetDialog.findViewById(R.id.edit_profile_textView_judul);
        TextInputLayout editProfileInput = bottomSheetDialog.findViewById(R.id.edit_profile_input);
        TextView editProfileBatal = bottomSheetDialog.findViewById(R.id.edit_profile_batal);
        TextView editProfileUbah = bottomSheetDialog.findViewById(R.id.edit_profile_ubah);

        editProfileTextViewJudul.setText(String.format(getString(R.string.edit_profile_textView_judul), field1));
        bottomSheetDialog.show();

        //Menutup BottomSheetDialog jika TextView "Batal" diklik
        editProfileBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        //Mencegah agar BottomSheetDialog tidak tertutup jika user klik area diluar BottomSheetDialog
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        //Mencegah agar BottomSheetDialog tidak tertutup melalui klik / swipe tombol back pada navigation gesture di smartphone
        bottomSheetDialog.setCancelable(false);

        editProfileInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //Mendeteksi perubahan setiap user mengetikkan input menggunakan TextWatcher
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (field1.equals("Username")) {
                    String username = editProfileInput.getEditText().getText().toString().trim();

                    //Regex Pattern untuk format username (kombinasi lowercase, angka, dan special characters)
                    Pattern USERNAME_PATTERN = Pattern.compile("[a-z0-9\\_\\.]{0,20}");

                    //Menampilkan warning message jika user mengirim input kosong & validasi format input
                    ValidateFormUtils valid = new ValidateFormUtils();
                    editProfileUbah.setEnabled(valid.validateField(username, editProfileInput, USERNAME_PATTERN.matcher(username).matches(), "Please fill the Username column.", "Wrong Username format."));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Jika Button "Ubah" diklik
        editProfileUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String saveValue = editProfileInput.getEditText().getText().toString();

                editUserData(saveField, saveValue, toastSuccessMessages, toastFailedMessages);
                bottomSheetDialog.dismiss();
            }
        });
    }

    //Menyimpan URL setiap foto profil ke Firestore pada masing-masing field nya user
    private void editUserData(String saveField, String saveValue, String toastSuccessMessages, String toastFailedMessages) {
        firebaseFirestore.collection("users").whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        DocumentReference documentReference = documentSnapshot.getReference();
                        documentReference.update(saveField, saveValue)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        getUserProfile();
                                        Toast.makeText(requireContext(), toastSuccessMessages, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(requireContext(), toastFailedMessages + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    //Menampilkan AlertDialog jika user klik "Log out"
    public void showAlertLogOutDialog() {
        String field1 = "Log out";
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle).create();
        View view = getLayoutInflater().inflate(R.layout.layout_alert_dialog, null);
        dialog.setView(view);

        //Mencegah agar AlertDialog tidak tertutup jika user klik area diluar AlertDialog maupun melalui klik / swipe tombol back pada navigation gesture di smartphone
        dialog.setCancelable(false);

        TextView alertTextViewJudul = view.findViewById(R.id.alert_textView_judul);
        TextView alertTextViewMessage = view.findViewById(R.id.alert_textView_message);
        TextView alertTextViewNegative = view.findViewById(R.id.alert_textView_negative);
        TextView alertTextViewPositive = view.findViewById(R.id.alert_textView_positive);

        //Menampilkan judul AlertDialog sesuai menu yang diklik
        alertTextViewJudul.setText(field1);
        //Menampilkan pertanyaan AlertDialog sesuai komponen yang diklik
        alertTextViewMessage.setText(String.format(getString(R.string.alert_textView_message), field1));
        //Menentukan action untuk Negative Button
        alertTextViewNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        //Menentukan action untuk Positive Button
        alertTextViewPositive.setText(field1);
        alertTextViewPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Membuat user menjadi log out jika telah konfirmasi "Log out" + Tampilkan Toast log out berhasil + Diarahkan ke LogInActivity
                firebaseAuth.signOut();
                Intent intent = new Intent(requireContext(), LogInActivity.class);
                startActivity(intent);
                //Mencegah user untuk kembali ke ProfileFragment karena telah berhasil log out
                (getActivity()).finish();
                Toast.makeText(requireContext(), "You are logged out.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    //Menampilkan AlertDialog jika user klik "Hapus Akun"
    public void showAlertHapusAkunDialog() {
        String field2 = "Hapus Akun";
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle).create();
        View view = getLayoutInflater().inflate(R.layout.layout_alert_dialog, null);
        dialog.setView(view);

        //Mencegah agar BottomSheetDialog tidak tertutup jika user klik area diluar BottomSheetDialog maupun melalui klik / swipe tombol back pada navigation gesture di smartphone
        dialog.setCancelable(false);

        TextView alertTextViewJudul = view.findViewById(R.id.alert_textView_judul);
        TextView alertTextViewMessage = view.findViewById(R.id.alert_textView_message);
        TextView alertTextViewNegative = view.findViewById(R.id.alert_textView_negative);
        TextView alertTextViewPositive = view.findViewById(R.id.alert_textView_positive);

        //Menampilkan judul AlertDialog sesuai menu yang diklik
        alertTextViewJudul.setText(field2);
        //Menampilkan pertanyaan AlertDialog sesuai komponen yang diklik
        alertTextViewMessage.setText(String.format(getString(R.string.alert_textView_message), field2));
        //Menentukan action untuk Negative Button
        alertTextViewNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        //Menentukan action untuk Positive Button
        alertTextViewPositive.setText(field2);
        alertTextViewPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDataUser();
            }
        });
        dialog.show();
    }

    //Melakukan eksekusi penghapusan akun jika seluruh document data user telah terhapus pada Firestore
    public void deleteAccount() {
        //Membuat akun user terhapus jika telah konfirmasi "Hapus Akun" + Tampilkan Toast hapus akun berhasil + Diarahkan ke SignUpActivity
        FirebaseUser user = firebaseAuth.getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(requireContext(), SignUpActivity.class);
                            startActivity(intent);
                            //Mencegah user untuk kembali ke ProfileFragment karena telah berhasil hapus akun
                            (getActivity()).finish();
                            Toast.makeText(requireContext(), "Account deleted.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Menghapus document pada Firestore
    public void deleteDataUser() {
        firebaseFirestore.collection("users").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot currentDocument = task.getResult().getDocuments().get(0);
                            DocumentReference documentReference = currentDocument.getReference();
                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    deleteAccount();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(requireContext(), "Delete Account failed. " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

}