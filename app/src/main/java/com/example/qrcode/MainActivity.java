package com.example.qrcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import  java.io.File;

import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity {
    private static final int MY_STOREGE_REQUEST_CODE =1 ;
    private ImageView imageViewBitmap;
    private ImageView imagem;
    EditText nome;
    File path;
    Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViewBitmap=findViewById(R.id.imageViewBitmap);
        imagem=findViewById(R.id.imageViewBitmap2);
        thread = new Thread();
        nome=findViewById(R.id.nome);


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_STOREGE_REQUEST_CODE);

        }

    }
    public void GerarQr(View view) {
        try {

            int width = 300;
            int height = 300;
            int smallestDimension = width < height ? width : height;

            EditText editText =findViewById(R.id.editText);
            String qrCodeData = editText.getText().toString();

            String charset = "UTF-8";
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            CriarQrCode(qrCodeData, charset, hintMap, smallestDimension, smallestDimension);

        } catch (Exception ex) {
            Log.e("GerarQr", ex.getMessage());
        }
    }

    public  void  CriarQrCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth){


        try {

            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);


            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {

                    pixels[offset + x] = matrix.get(x, y) ?
                            ResourcesCompat.getColor(getResources(), R.color.colorB, null) : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.mipmap.git);
            imageViewBitmap.setImageBitmap( juntarbitmaps(overlay,bitmap));






        }catch (Exception er){
            Log.e("CriarQrCode",er.getMessage());
        }
    }



    public Bitmap juntarbitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        save(combined);
        return combined;
    }public void save(Bitmap bmp){


        path= Environment.getExternalStorageDirectory();
        File dir= new File(path + "/GenerateQrCode/");
        if(!dir.isDirectory()){
            dir.mkdir();
        }

        File file= new File(dir,nome.getText().toString()+".png");
        OutputStream out;
        try{
            out=new FileOutputStream(file);
            if(bmp.compress(Bitmap.CompressFormat.JPEG,100, out))
            {
                Toast saved = Toast.makeText(getApplicationContext(), "Image saved.", Toast.LENGTH_LONG);
                saved.show();
            }
            else{
                Toast unsaved = Toast.makeText(getApplicationContext(), "Image not save.", Toast.LENGTH_LONG);
                unsaved.show();
            }


        }catch (Exception e){
            e.printStackTrace();}


    }
}
