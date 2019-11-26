package com.example.application.ui.merger;

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

public class MergerFragment extends Fragment {

    private MergerViewModel mergerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mergerViewModel =
                ViewModelProviders.of(this).get(MergerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_merger, container, false);

        return root;
    }
}