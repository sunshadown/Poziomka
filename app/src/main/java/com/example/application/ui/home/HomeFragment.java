package com.example.application.ui.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.application.MainActivity;
import com.example.application.R;
import com.example.application.TessOcr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Button button;
    private byte[] bytes;
    private ImageView myImage;
    private Bitmap myBitmap;
    private ProgressDialog mProgressDialog;
    private TessOcr mTessOCR;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.button);

        String language = "eng";
        mTessOCR = new TessOcr(getContext(),language);

        button = root.findViewById(R.id.button);
        myImage = root.findViewById(R.id.imageView2);
        readImage();


        homeViewModel.getButtonText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void readImage(){
        //File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "ciasteczko");
        //if (mediaStorageDir.exists()) {
            try {
                //final File file = new File(mediaStorageDir, "/pic.jpg");
                final File file = new File(getContext().getFilesDir(), "/pic.jpg");
                FileInputStream input = new FileInputStream(file);
                int size = (int) file.length();
                bytes = new byte[size];
                input.read(bytes, 0, size);
                if(isStoragePermissionGranted()){
                    myBitmap = BitmapFactory.decodeFile(file.getPath());
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90.0f);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
                    myImage.setImageBitmap(rotatedBitmap);
                    //Toast.makeText(getContext(), "setting bitmap", Toast.LENGTH_LONG).show();
                    doOCR(rotatedBitmap);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}
    }

    private void doOCR(final Bitmap bitmap) {
        /*if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getContext(), "Processing",
                    "Doing OCR...", true);
        } else {
            mProgressDialog.show();
        }*/
        final String srcText = mTessOCR.getOCRResult(bitmap);
        Toast.makeText(getContext(), srcText, Toast.LENGTH_LONG).show();
        /*new Thread(new Runnable() {
            public void run() {
                final String srcText = mTessOCR.getOCRResult(bitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (srcText != null && !srcText.equals("")) {
                            ocrText.setText(srcText);
                        }
                        mProgressDialog.dismiss();
                    }
                });
            }
        }).start();*/
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
}