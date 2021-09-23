package com.cahyaa.chatapp.data.membership.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cahyaa.chatapp.utils.ResultState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

public class MembershipFirebaseImpl implements MembershipService {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public void instanceFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public LiveData<ResultState<Boolean>> signUp(String email, String password) {
        instanceFirebase();

        MutableLiveData<ResultState<Boolean>> result = new MutableLiveData<>();

        result.postValue(new ResultState<>(true));

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            result.postValue(new ResultState(true, false));
                        } else {
                            result.postValue(new ResultState(task.getException(), false));
                        }
                    }
                });
        return result;
    }

    @Override
    public LiveData<ResultState<Boolean>> saveDataUser() {
        return null;
    }
}
