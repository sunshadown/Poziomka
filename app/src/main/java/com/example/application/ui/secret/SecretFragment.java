package com.example.application.ui.secret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.application.R;

public class SecretFragment extends Fragment {

    private SecretViewModel secretViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        secretViewModel =
                ViewModelProviders.of(this).get(SecretViewModel.class);
        View root = inflater.inflate(R.layout.fragment_secret, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        secretViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}