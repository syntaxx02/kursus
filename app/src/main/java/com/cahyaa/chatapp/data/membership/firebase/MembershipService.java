package com.cahyaa.chatapp.data.membership.firebase;

import androidx.lifecycle.LiveData;

import com.cahyaa.chatapp.utils.ResultState;

public interface MembershipService {
    LiveData<ResultState<Boolean>> signUp(String email, String password);
    LiveData<ResultState<Boolean>> saveDataUser();
}