package com.cahyaa.chatapp.presentation.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.cahyaa.chatapp.data.membership.firebase.MembershipFirebaseImpl;
import com.cahyaa.chatapp.utils.ResultState;

public class SignUpViewModel extends ViewModel {
    MembershipFirebaseImpl repository;

    //Constructor
    public SignUpViewModel(MembershipFirebaseImpl repository) {
        this.repository = repository;
    }

    public LiveData<ResultState<Boolean>> signUp(String email, String password) {
        return repository.signUp(email, password);
    }

    public LiveData<ResultState<Boolean>> saveDataUser() {
        return repository.saveDataUser();
    }
}