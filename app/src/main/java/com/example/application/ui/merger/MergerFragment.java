package com.example.application.ui.merger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

import com.airbnb.lottie.LottieAnimationView;
import com.example.application.BuildConfig;
import com.example.application.R;
import com.example.application.TessOcr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import 	java.io.BufferedReader;

public class MergerFragment extends Fragment {

    private MergerViewModel mergerViewModel;
    private LottieAnimationView lottieAnimationView;
    private final int pickimage_code = 1;
    private final int picktxt_code = 2;
    private ProgressDialog mProgressDialog;
    private TessOcr mTessOCR;
    private String ocr_text;
    private TextView merge_password;
    private ImageView s1;
    private Bitmap s1_bitmap;
    private TextView s2;
    private Switch legacy_switch;
    private boolean switch_state = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mergerViewModel =
                ViewModelProviders.of(this).get(MergerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_merger, container, false);

        merge_password = root.findViewById(R.id.merge_password);

        lottieAnimationView = root.findViewById(R.id.merger_lottie);
        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(s1_bitmap == null){
                    return;
                }
                doOCR(s1_bitmap);
            }
        });

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

        String language = "eng";
        mTessOCR = new TessOcr(getContext(),language);

        return root;
    }

    private void doOCR(final Bitmap bitmap) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getContext(), "Processing",
                    "Doing OCR...", true);
        } else {
            mProgressDialog.show();
        }
        final String srcText = mTessOCR.getOCRResult(bitmap);
        ocr_text = "";

        new Thread(new Runnable() {
            public void run() {
                final String srcText = mTessOCR.getOCRResult(bitmap);
                s1.post(new Runnable() {
                    @Override
                    public void run() {

                        if (srcText != null && !srcText.equals("")) {
                            ocr_text = srcText;
                            Toast.makeText(getContext(), ocr_text + ", " + ocr_text.length(), Toast.LENGTH_SHORT).show();
                            String decoded = DecodeOcrStream(ocr_text);
                            Toast.makeText(getContext(), decoded + ", " + decoded.length(), Toast.LENGTH_SHORT).show();
                            String haslo = GenerateShadow(decoded, (String) s2.getText());
                            merge_password.setText(haslo);
                        }
                        mProgressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    private String DecodeOcrStream(String ocrstr){
        if(ocrstr.length() < 13 * 3){
            Toast.makeText(getContext(), "Wrong ocrstr size" + ", size: " + ocrstr.length(), Toast.LENGTH_SHORT).show();
            return "";
        }
        Log.e("ocr",ocrstr);
        final int length = 12;
        String ocrdecoded = new String();
        for (int i = 0; i < 13*3; i+= 3) {
            char t1 = ocr_text.charAt(i);
            char t2 = ocr_text.charAt(i + 1);
            char t3 = ocr_text.charAt(i + 2);
            String tstring = new String();
            tstring += t1;
            tstring += t2;
            tstring += t3;
            Log.e("ocr_string",tstring);

            char t = (char) (Integer.parseInt(tstring));
            Log.e("ocr_char", String.valueOf(t));

            ocrdecoded += t;
        }
        return ocrdecoded;
    }

    private String GenerateShadow(String pass, String seq)
    {
        String output = new String();
        final int length = 12 + 1;
        final int pass_length = pass.length();

        if (pass_length < length || seq.length() < length){
            Toast.makeText(getContext(), "Wrong lengths(s1,s2): " + pass + " " + pass_length + " " + seq.length(), Toast.LENGTH_SHORT).show();
            return "";
            //todo drop app
        }

        for (int i = 0; i < length; i++) {
            char l = pass.charAt(i);
            char k = seq.charAt(i);
            char t = (char)(l ^ k);
            output += (char)t;
        }
        int genpass_len = output.charAt(length - 1);
        if(genpass_len == 0){
            return "";
        }
        if(genpass_len == 12){
            return  output;
        }
        String outdata = output.substring(0,genpass_len);
        return outdata;
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
            //s1.setImageURI(uri);
            try {
                FileInputStream input = new FileInputStream(getContext().getContentResolver().openFileDescriptor(uri,"r").getFileDescriptor());
                s1_bitmap = BitmapFactory.decodeStream(input);
                s1.setImageBitmap(s1_bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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