package com.example.application.ui.secret;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecretFragment extends Fragment {

    private SecretViewModel secretViewModel;
    private Button generate_button;
    private Button share_s1;
    private Button share_s2;
    private TextView password_texview;
    private TextView shadow1;
    private TextView shadow2;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        secretViewModel = ViewModelProviders.of(this).get(SecretViewModel.class);
        View root = inflater.inflate(R.layout.fragment_secret, container, false);

        share_s1 = root.findViewById(R.id.shadow1_share);
        share_s2 = root.findViewById(R.id.shadow2_share);
        shadow1 = root.findViewById(R.id.shadow1);
        shadow2 = root.findViewById(R.id.shadow2);
        shadow1.setText("Shadow_1: ");
        shadow2.setText("Shadow_2: ");

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

        share_s1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getContext().getFilesDir(),"shadows");
                if(file.exists()){
                    File s1 = new File(file,"/shadow1.jpg");
                    SendShadow1(s1);
                }
            }
        });


        share_s2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getContext().getFilesDir(),"shadows");
                if(file.exists()){
                    File s2 = new File(file,"/shadow2.txt");
                    SendShadow2(s2);
                }
            }
        });

        generate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password_texview.getText().length() == 0){
                    return;
                }
                String seq = GenerateSequence();
                //Toast.makeText(getContext(), "seq: " + seq +" length:" + seq.length(),Toast.LENGTH_SHORT).show();
                String shadow = GenerateShadow(password_texview.getText().toString(), seq);
                //Toast.makeText(getContext(), "shadow: "+shadow + " length:" + shadow.length(),Toast.LENGTH_SHORT).show();
                //String val = GenerateShadow(shadow, seq);
                //Toast.makeText(getContext(),"val: "+val + " length:" + val.length() ,Toast.LENGTH_SHORT).show();
                shadow1.setText("Shadow_1: " + seq);
                shadow2.setText("Shadow_2: " + shadow);
                File file = new File(getContext().getFilesDir(),"shadows");
                if(!file.exists())
                {
                    file.mkdirs();
                }
                OutputStream output = null;
                File shadow1_file = new File(file, "/shadow1.jpg");
                File shadow2_file = new File(file,"/shadow2.txt");
                StringBuilder text = new StringBuilder();
                try {
                    output = new FileOutputStream(shadow2_file);
                    output.write(shadow.getBytes());
                    BufferedReader br = new BufferedReader(new FileReader(shadow2_file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        //text.append('\n');
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != output) {
                        try {
                            output.close();
                            String result = text.toString();
                            Toast.makeText(getContext(),result + " len:" + result.length(),Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Bitmap bitmap = CreateBitmap();
                Canvas canvas=new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setColor(Color.BLACK); // Text Color
                paint.setTextSize(78); // Text Size
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                canvas.drawBitmap(bitmap, 0, 0, paint);
                canvas.drawText(seq, 180, 300, paint);
                SaveBitmap(bitmap);
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

    private Bitmap CreateBitmap(){
        Bitmap image = Bitmap.createBitmap(800, 600 , Bitmap.Config.ARGB_8888);
        image.eraseColor(Color.WHITE);
        return image;
    }

    private void SaveBitmap(Bitmap bitmap){
        File file = new File(getContext().getFilesDir(),"shadows");
        if(!file.exists())
        {
            file.mkdirs();
        }
        File dest = new File(file, "shadow1.jpg");
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void SendShadow1(File shadow1_file){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/image");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Shadow_1");
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, currentDateTimeString);
        //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(shadow1_file.getAbsolutePath()));

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
        Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, shadow1_file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        PackageManager pm = getActivity().getPackageManager();
        if (emailIntent.resolveActivity(pm) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
    }

    private void SendShadow2(File shadow2_file){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/image");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Shadow_2");
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, currentDateTimeString);
        //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(shadow1_file.getAbsolutePath()));

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
        Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, shadow2_file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        PackageManager pm = getActivity().getPackageManager();
        if (emailIntent.resolveActivity(pm) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
    }
}