package com.orion.pos_crushty_android.ui.penjualan;

import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.detail_barang_jual.DetailBarangJualModel;
import com.orion.pos_crushty_android.databases.keranjang_detail.KeranjangDetailModel;
import com.orion.pos_crushty_android.databases.keranjang_detail.KeranjangDetailTable;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterModel;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterTable;
import com.orion.pos_crushty_android.databases.master_barang_jual.MasterBarangJualModel;
import com.orion.pos_crushty_android.databases.setting_komposisi_det.SettingKomposisiDetModel;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.squareup.picasso.Picasso;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ProductDetailBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_ITEM = "item";
    private OnItemAddedListener listener;
    public void setOnItemAddedListener(OnItemAddedListener listener) {
        this.listener = listener;
    }
    public interface OnItemAddedListener {
        void onItemAdded();
    }


    public static ProductDetailBottomSheet newInstance(MasterBarangJualModel item) {
        ProductDetailBottomSheet fragment = new ProductDetailBottomSheet();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_product, container, false);

        //createview
        TextView tvNamaBarang = view.findViewById(R.id.tvNamaBarang);
        TextView tvHarga = view.findViewById(R.id.tvHarga);
        TextView tvMinus = view.findViewById(R.id.tvMinus);
        TextView tvPlus = view.findViewById(R.id.tvPlus);
        EditText txtQty = view.findViewById(R.id.txtQty);
        Button btnSimpan = view.findViewById(R.id.btnSimpan);
        ImageView imgBarang = view.findViewById(R.id.imgBarang);
        CardView cardView = view.findViewById(R.id.cardView); // ganti dengan ID yang sesuai

        // Mengatur tinggi CardView sesuai lebar layar agar bentuk kotak
        // Mengecek orientasi layar
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Hanya diorientasi portrait: Mengatur tinggi CardView sesuai lebar layar agar bentuk kotak
            ViewTreeObserver viewTreeObserver = cardView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Mendapatkan lebar dari CardView
                    int width = cardView.getWidth();

                    // Mengatur tinggi sama dengan lebar
                    cardView.getLayoutParams().height = width;
                    cardView.requestLayout();

                    // Menghapus listener setelah eksekusi untuk mencegah loop
                    cardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        //initialis
        txtQty.setText("1");

        if (getArguments() != null) {
            MasterBarangJualModel item = getArguments().getParcelable(ARG_ITEM);
            if (item != null) {
                tvNamaBarang.setText(item.getNama());
                tvHarga.setText(String.valueOf(item.getHarga()));
                if (item.getKomposisiHarga() > 0) {
                    tvHarga.setText(Global.FloatToStrFmt(item.getKomposisiHarga(), true));
                }else{
                    tvHarga.setText(Global.FloatToStrFmt(item.getHarga(), true));
                }

                if (!TextUtils.isEmpty(item.getGambar())) {
                    File imgFile = new File(JConst.downloadPath + item.getGambar());
                    if (imgFile.exists()) {
                        Picasso.get().load(imgFile).into(imgBarang);
                    }
                }

                //event
                tvMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (txtQty.getText().toString().equals("0")) return;

                        double qtyTemp = Global.StrFmtToFloat(txtQty.getText().toString()) - 1;
                        txtQty.setText(Global.FloatToStrFmt(qtyTemp, false));
                    }
                });

                tvPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        double qtyTemp = Global.StrFmtToFloat(txtQty.getText().toString())+ 1;
                        txtQty.setText(Global.FloatToStrFmt(qtyTemp, false));
                    }
                });

                btnSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Global.checkShift(getActivity(), () -> {
                            Global.checkPrintZ(getActivity(), () -> {
                                if (txtQty.getText().toString().equals("0")) return;
                                double qtyTemp = Global.StrFmtToFloat(txtQty.getText().toString());
                                saveKeranjang(qtyTemp, item.getUid(), item.getDetailIds(), item.getKomposisiIds());

                                if (listener != null) {
                                    listener.onItemAdded();
                                }
                                dismiss();
                            });
                        });
                    }
                });

            }
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Mengatur BottomSheetBehavior di sini
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

            // Menghitung tinggi 90% dari layar
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            int maxHeight = (int) (height * 0.9); // 90% dari tinggi layar

            // Mengatur peek height dan state bottom sheet
            bottomSheetBehavior.setPeekHeight(maxHeight);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Membuat bottom sheet terbuka sepenuhnya
        }
    }



    private void saveKeranjang(double qtyInput, String barangJualUid, List<DetailBarangJualModel> detailIds, List<SettingKomposisiDetModel> komposisiIds) {
        // Membuka koneksi ke database
        KeranjangMasterTable keranjangMasterTable = new KeranjangMasterTable(getContext());
        KeranjangDetailTable keranjangDetailTable = new KeranjangDetailTable(getContext());

        //cek apakah barang sudah ada dikeranjang?
        int seqKeranjang = keranjangMasterTable.getSeqKeranjang(barangJualUid);
        if (seqKeranjang > 0){
            keranjangMasterTable.updateQtyKeranjangMasterDetail(seqKeranjang, qtyInput);
            return; //stop
        }

        // Membuat model master untuk disimpan ke dalam keranjang_master
        KeranjangMasterModel masterModel = new KeranjangMasterModel();
        masterModel.setBarangJualUid(barangJualUid);
        masterModel.setQty(qtyInput);

        // Simpan masterModel ke dalam tabel keranjang_master
        long masterSeq = keranjangMasterTable.insert(masterModel); // Menggunakan metode insert yang mengembalikan seq

        //cek jika barang ada setting komposisi
        if (komposisiIds != null){
            // Loop melalui setiap item dalam detailIds
            for (SettingKomposisiDetModel komposisiDetModel : komposisiIds) {

                // Membuat model detail untuk disimpan ke dalam keranjang_detail
                KeranjangDetailModel detailModel = new KeranjangDetailModel();
                detailModel.setMasterSeq((int) masterSeq); // Set seq master
                detailModel.setBarangUid(komposisiDetModel.getBarangUid());
                detailModel.setQty(qtyInput);
                detailModel.setQty_default(komposisiDetModel.getQty()); //set sesuai qty komposisi untuk backend

                // Simpan detailModel ke dalam tabel keranjang_detail
                keranjangDetailTable.insert(detailModel);
            }
        }else {
            // Loop melalui setiap item dalam detailIds
            for (DetailBarangJualModel detail : detailIds) {

                // Membuat model detail untuk disimpan ke dalam keranjang_detail
                KeranjangDetailModel detailModel = new KeranjangDetailModel();
                detailModel.setMasterSeq((int) masterSeq); // Set seq master
                detailModel.setBarangUid(detail.getBarangUid());
                detailModel.setQty(qtyInput);
                detailModel.setQty_default(detail.getQty());    //set sesuai qty komposisi untuk backend

                // Simpan detailModel ke dalam tabel keranjang_detail
                keranjangDetailTable.insert(detailModel);
            }
        }
    }
}
