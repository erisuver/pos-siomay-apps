package com.orion.pos_crushty_android.ui.penjualan;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.orion.pos_crushty_android.JApplication;
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
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PenjualanAdapter  extends RecyclerView.Adapter {
    Context context;
    List<MasterBarangJualModel> Datas;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    PenjualanFragment penjualanFragment;
    private ProgressDialog Loading;
    private int ViewProductList;

    public PenjualanAdapter(Context context, List<MasterBarangJualModel> masterBarangJualModel, PenjualanFragment penjualanFragment, int view) {
        this.context = context;
        this.Datas = masterBarangJualModel;
        this.penjualanFragment = penjualanFragment;
        this.Loading = new ProgressDialog(context);
        this.ViewProductList = view;
    }

    public void addModels(List<MasterBarangJualModel> masterBarangJualModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(masterBarangJualModel);
        notifyItemRangeInserted(pos, masterBarangJualModel.size());
    }

    public void addModel(MasterBarangJualModel productListModel) {
        this.Datas.add(productListModel);
        notifyItemRangeInserted(Datas.size()-1,Datas.size()-1);
    }

    public void removeModel(int idx) {
        if (Datas.size() > 0){
            this.Datas.remove(Datas.size()-1);
            notifyItemRemoved(idx);
            notifyItemRangeChanged(idx, Datas.size());
        }
    }

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        notifyItemRangeRemoved(0, LastPosition);
    }

    public void SetJenisView(int view){
        this.ViewProductList = view;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(ViewProductList, parent, false);
            return new ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder){
            final MasterBarangJualModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.txtNama.setText(mCurrentItem.getNama());
            if (mCurrentItem.getKomposisiHarga() > 0) {
                itemHolder.txtHarga.setText(Global.FloatToStrFmt(mCurrentItem.getKomposisiHarga(), true));
            }else{
                itemHolder.txtHarga.setText(Global.FloatToStrFmt(mCurrentItem.getHarga(), true));
            }

            if (!TextUtils.isEmpty(mCurrentItem.getGambar())) {
                File imgFile = new File(JConst.downloadPath + mCurrentItem.getGambar());
                if (imgFile.exists()) {
                    Picasso.get().load(imgFile).into(itemHolder.imgBarang);
                }
            }
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductDetailBottomSheet bottomSheet = ProductDetailBottomSheet.newInstance(mCurrentItem);

                    bottomSheet.setOnItemAddedListener(new ProductDetailBottomSheet.OnItemAddedListener() {
                        @Override
                        public void onItemAdded() {
                            Snackbar.make(view, "Item berhasil ditambahkan ke keranjang.", Snackbar.LENGTH_SHORT).show();
                            MainActivity.setInitMainActivity(false);
                        }
                    });

                    bottomSheet.show(penjualanFragment.getActivity().getSupportFragmentManager(), "ProductDetailBottomSheet");
                }
            });


            itemHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Global.checkShift(penjualanFragment.getActivity(), () -> {
                        Global.checkPrintZ(penjualanFragment.getActivity(), () -> {
                            saveKeranjang(mCurrentItem.getUid(), mCurrentItem.getDetailIds(), mCurrentItem.getKomposisiIds());
                            Snackbar.make(view, "1 item berhasil ditambahkan ke keranjang.", Snackbar.LENGTH_SHORT).show();
                            MainActivity.setInitMainActivity(false);
                        });
                    });
                }
            });

        }else if (holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return Datas.get(position) == null ? VIEW_TYVE_LOADING : VIEW_TYVE_ITEM;
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtHarga;
        ImageView imgBarang;
        ImageButton btnAdd;
        public ItemHolder(View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNamaBarang);
            txtHarga = itemView.findViewById(R.id.txtHarga);
            imgBarang = itemView.findViewById(R.id.imgBarang);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    private void saveKeranjang(String barangJualUid, List<DetailBarangJualModel> detailIds, List<SettingKomposisiDetModel> komposisiIds) {
        // Membuka koneksi ke database
        KeranjangMasterTable keranjangMasterTable = new KeranjangMasterTable(context);
        KeranjangDetailTable keranjangDetailTable = new KeranjangDetailTable(context);

        //cek apakah barang sudah ada dikeranjang?
        int seqKeranjang = keranjangMasterTable.getSeqKeranjang(barangJualUid);
        if (seqKeranjang > 0){
            keranjangMasterTable.updateQtyKeranjangMasterDetail(seqKeranjang);
            return; //stop
        }

        // Membuat model master untuk disimpan ke dalam keranjang_master
        KeranjangMasterModel masterModel = new KeranjangMasterModel();
        masterModel.setBarangJualUid(barangJualUid);
        masterModel.setQty(1);

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
                detailModel.setQty(1);  //set 1 untuk ui
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
                detailModel.setQty(1);  //set 1 untuk ui
                detailModel.setQty_default(detail.getQty());    //set sesuai qty komposisi untuk backend

                // Simpan detailModel ke dalam tabel keranjang_detail
                keranjangDetailTable.insert(detailModel);
            }
        }
    }

}