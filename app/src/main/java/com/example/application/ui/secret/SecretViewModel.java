package com.example.application.ui.secret;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SecretViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<String> mGenerateText;
    private MutableLiveData<String> mPasswordText;

    public SecretViewModel() {
        mText = new MutableLiveData<>();
        mGenerateText = new MutableLiveData<>();
        mPasswordText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
        getmGenerateText().setValue("Generate");
        mPasswordText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<String> getmPasswordText() {
        return mPasswordText;
    }

    public MutableLiveData<String> getmGenerateText() {
        return mGenerateText;
    }
}