package com.example.application.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<String> button_text;
    private MutableLiveData<String> ocr_text;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        button_text = new MutableLiveData<>();
        ocr_text = new MutableLiveData<>();

        ocr_text.setValue("None");
        mText.setValue("This is home fragment");
        button_text.setValue("Take Image");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getButtonText(){
        return button_text;
    }

    public LiveData<String> getOCRtext(){return ocr_text;}
}