package com.example.application.ui.merger;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.application.BuildConfig;
import com.example.application.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import 	java.io.BufferedReader;

public class MergerFragment extends Fragment {

    private MergerViewModel mergerViewModel;
    private final int pickimage_code = 1;
    private final int picktxt_code = 2;
    private ImageView s1;
    private TextView s2;
    private Switch legacy_switch;
    private boolean switch_state = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mergerViewModel =
                ViewModelProviders.of(this).get(MergerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_merger, container, false);

        s2 = root.findViewById(R.id.merger_s2);
        s2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickText();
            }
        });

        legacy_switch = root.findViewById(R.id.legacy);
        legacy_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_state = !switch_state;

                if(switch_state){

                }
                else {

                }
            }
        });


        s1 = root.findViewById(R.id.merger_s1);
        s1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImage();
            }
        });


        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pickimage_code && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Uri uri = data.getData();
            s1.setImageURI(uri);
        }

        if (requestCode == picktxt_code && resultCode == Activity.RESULT_OK) {
            Uri txt_uri = data.getData();
            String filepath = txt_uri.getPath();
            ContentResolver contentResolver = getContext().getContentResolver();
            InputStream inputStream = null;
            try {
                inputStream = contentResolver.openInputStream(txt_uri);
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line);//.append('\n');
                }
                String datastring = total.toString();
                //Toast.makeText(getContext(),datastring,Toast.LENGTH_SHORT).show();
                s2.setText(datastring);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void PickImage(){

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, pickimage_code);
    }

    public void PickText(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(Intent.createChooser(intent,"Select txt"), picktxt_code);
    }
}