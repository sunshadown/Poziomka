package com.example.application;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TessOcr {
    private final TessBaseAPI mTess;

    public TessOcr(Context context, String lang){
        mTess = new TessBaseAPI();
        boolean fileExistFlag = false;

        AssetManager assetManager = context.getAssets();
        String dstPathDir = "/tesseract/tessdata/";
        String srcFile = "eng.traineddata";
        dstPathDir = context.getFilesDir() + dstPathDir;
        String dstInitPathDir = context.getFilesDir() + "/tesseract";
        String dstPathFile = dstPathDir + srcFile;

        InputStream inFile = null;
        FileOutputStream outFile = null;

        try {
            inFile = assetManager.open(srcFile);

            File f = new File(dstPathDir);

            if (!f.exists()) {
                if (!f.mkdirs()) {
                    Toast.makeText(context, srcFile + " can't be created.", Toast.LENGTH_SHORT).show();
                }
                outFile = new FileOutputStream(new File(dstPathFile));
            } else {
                fileExistFlag = true;
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();

        } finally {

            if (fileExistFlag) {
                try {
                    if (inFile != null) inFile.close();
                    mTess.init(dstInitPathDir, lang);
                    Toast.makeText(context, "tesseract init", Toast.LENGTH_SHORT).show();
                    return;

                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            if (inFile != null && outFile != null) {
                try {
                    //copy file
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inFile.read(buf)) != -1) {
                        outFile.write(buf, 0, len);
                    }
                    inFile.close();
                    outFile.close();
                    mTess.init(dstInitPathDir, lang);
                    Toast.makeText(context, "tesseract init", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, srcFile + " can't be read.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getOCRResult(Bitmap bitmap) {
        mTess.setImage(bitmap);
        return mTess.getUTF8Text();
    }

    public void onDestroy() {
        if (mTess != null) mTess.end();
    }
}
