package com.example.application.ui.secret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.application.R;

public class SecretFragment extends Fragment {

    private SecretViewModel secretViewModel;
    private Button generate_button;
    private TextView password_texview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        secretViewModel = ViewModelProviders.of(this).get(SecretViewModel.class);
        View root = inflater.inflate(R.layout.fragment_secret, container, false);

        generate_button = root.findViewById(R.id.generate_shadows);
        secretViewModel.getmGenerateText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                generate_button.setText(s);
            }
        });

        password_texview = root.findViewById(R.id.password);
        secretViewModel.getmPasswordText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                password_texview.setText(s);
            }
        });


        generate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seq = GenerateSequence();
                Toast.makeText(getContext(), "seq: " + seq +" length:" + seq.length(),Toast.LENGTH_SHORT).show();
                String shadow = GenerateShadow(password_texview.getText().toString(), seq);
                Toast.makeText(getContext(), "shadow: "+shadow + " length:" + shadow.length(),Toast.LENGTH_SHORT).show();
                String val = GenerateShadow(shadow, seq);
                Toast.makeText(getContext(),"val: "+val + " length:" + val.length() ,Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void DisplayPass()
    {
        Toast.makeText(getContext(), password_texview.getText(),Toast.LENGTH_SHORT).show();
    }

    private String GenerateSequence()
    {
        String output = new String();
        //int max = 65535;
        int max = 122;
        int min = 33;
        final int length = 12;
        for (int i = 0; i < length; i++) {
            char t = (char) Math.floor((Math.random() * ((max - min) + 1)) + min);
            output += t;
        }
        return output;
    }

    private String GenerateShadow(String pass, String seq)
    {
        String output = new String();
        final int length = 12;
        final int pass_length = pass.length();

        if (pass_length < length){
            for (int i = pass_length; i < length; i++) {
                pass+= '0';
            }
        }

        for (int i = 0; i < 12; i++) {
            char l = pass.charAt(i);
            char k = seq.charAt(i);
            char t = (char)(l ^ k);
            output += (char)t;
        }
        return output;
    }
}