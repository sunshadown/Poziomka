package com.example.application.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<String> button_text;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        button_text = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        button_text.setValue("Take Image");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getButtonText(){
        return button_text;
    }
}