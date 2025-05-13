package com.orion.pos_crushty_android.ui.pembayaran;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.globals.FileUtil;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PembayaranQRISActivity extends AppCompatActivity {
    //var componen/view
    private Button btnLanjut, btnDelete;
    private Toolbar toolbar;
    private TextView toolbarTitle, tvTotal;
    private ImageView imgUpload;

    //var global
    private double totalTagihan;
    private Uri imageUri;
    private String imagePath = "";
    private String currentPhotopath;
    private String fileName = "";
    private final int REQ_CODE_CAMERA = 1;
    private final int REQ_CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_qris);
        JApplication.currentActivity = this;
        CreateView();
        InitClass();
        EventClass();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void CreateView() {
        btnLanjut = findViewById(R.id.btnLanjut);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvTotal = findViewById(R.id.tvTotal);
        imgUpload = findViewById(R.id.imgUpload);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void InitClass() {
        Bundle extra = this.getIntent().getExtras();
        totalTagihan = extra.getDouble("total");
        tvTotal.setText(Global.FloatToStrFmt(totalTagihan,true));
        btnDelete.setVisibility(View.INVISIBLE);
    }

    private void EventClass() {
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String penjualanMstUid = GlobalTable.savePenjualan(getApplicationContext(), JConst.TIPE_BAYAR_QRIS, totalTagihan, totalTagihan, 0, imagePath);

                Intent intent = new Intent(PembayaranQRISActivity.this, PembayaranSuccessActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("tipe_bayar", JConst.TIPE_BAYAR_QRIS);
                intent.putExtra("penjualan_mst_uid", penjualanMstUid);
                startActivity(intent);
            }
        });

        imgUpload.setOnClickListener(view -> {
            showPictureDialog();
        });

        btnDelete.setOnClickListener(view -> {
            fileName = "";
            imgUpload.setImageBitmap(null);
            btnDelete.setVisibility(View.GONE);
        });
    }

    private void showPictureDialog(){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_dialog_picture, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(PembayaranQRISActivity.this);
        alert.setView(alertLayoutDetail);
        alert.setCancelable(true);

        final AlertDialog dialogDet = alert.create();
        dialogDet.show();
        dialogDet.setCancelable(true);
        dialogDet.setCanceledOnTouchOutside(true);

        final CardView gallery = alertLayoutDetail.findViewById(R.id.crdGalery);
        final CardView camera = alertLayoutDetail.findViewById(R.id.crdCamera);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galeryPicture();
                dialogDet.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPicture();
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }

    private void cameraPicture() {
        String fileName = "photo";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(fileName,".jpg", storageDir);
            currentPhotopath = imageFile.getAbsolutePath();
            imageUri = FileProvider.getUriForFile(PembayaranQRISActivity.this,
                    getApplicationContext().getPackageName() + ".provider", imageFile);

            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePicture, REQ_CODE_CAMERA);

            imagePath = currentPhotopath;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void galeryPicture() {
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, REQ_CODE_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = this;
        switch(requestCode) {
            //ambil dari camera
            case REQ_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        imgUpload.setImageBitmap(Global.handleSamplingAndRotationBitmap(this, imageUri));
                        btnDelete.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    imagePath = "";
                    imageUri = null;
                }
                break;

            //ambil dari galery
            case REQ_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    imagePath = FileUtil.getFilePathFromURI(imageUri, this);

                    try {
                        imgUpload.setImageBitmap(Global.handleSamplingAndRotationBitmap(this, imageUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }
}