package com.orion.pos_crushty_android.ui.penjualan_keranjang;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterModel;
import com.orion.pos_crushty_android.databases.keranjang_master.KeranjangMasterTable;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class PenjualanKeranjangAdapter extends RecyclerView.Adapter {
    Context context;
    List<KeranjangMasterModel> Datas;
    PenjualanKeranjangActivity penjualanKeranjangActivity;
    KeranjangMasterTable keranjangMasterTable;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private double totalPesananTemp = 0;

    public PenjualanKeranjangAdapter(PenjualanKeranjangActivity penjualanKeranjangActivity, List<KeranjangMasterModel> keranjangModels, int view) {
        this.context = penjualanKeranjangActivity.getApplicationContext();
        this.penjualanKeranjangActivity = penjualanKeranjangActivity;
        this.Datas = keranjangModels;
        this.view = view;
        this.keranjangMasterTable = new KeranjangMasterTable(context);
        this.totalPesananTemp = keranjangMasterTable.getTotalPesanan();
        this.penjualanKeranjangActivity.setTotalPesanan(totalPesananTemp);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new ItemHolder(row);
        } else if (viewType == VIEW_TYVE_LOADING) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder) {
            final KeranjangMasterModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            if (!TextUtils.isEmpty(mCurrentItem.getGambar())) {
                File imgFile = new File(JConst.downloadPath + mCurrentItem.getGambar());
                if (imgFile.exists()) {
                    Picasso.get().load(imgFile).into(itemHolder.imgBarang);
                }
            }

            itemHolder.tvNamaBarang.setText(mCurrentItem.getNamaBarang());
            itemHolder.tvHarga.setText(Global.FloatToStrFmt(mCurrentItem.getHarga(), true));
            itemHolder.txtQty.setText(Global.FloatToStrFmt(mCurrentItem.getQty(), false));

            itemHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(penjualanKeranjangActivity, CustomPesananActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.putExtra("seq", mCurrentItem.getSeq());
                    penjualanKeranjangActivity.startActivity(i);
                }
            });

            itemHolder.tvMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qtyTemp = (int) (mCurrentItem.getQty() - 1);
                    mCurrentItem.setQty(qtyTemp);
                    itemHolder.txtQty.setText(Global.FloatToStrFmt(mCurrentItem.getQty(), false));
                    totalPesananTemp -= mCurrentItem.getHarga();
                    penjualanKeranjangActivity.setTotalPesanan(totalPesananTemp);
                    if (qtyTemp <= 0) {
                        keranjangMasterTable.deleteKeranjang(mCurrentItem.getSeq());
                        penjualanKeranjangActivity.LoadData();
                        MainActivity.setInitMainActivity(false);
                    }else{
                        keranjangMasterTable.updateQtyKeranjang(mCurrentItem.getSeq(), qtyTemp);
                    }
                }
            });

            itemHolder.tvPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qtyTemp = (int) (mCurrentItem.getQty() + 1);
                    mCurrentItem.setQty(qtyTemp);
                    itemHolder.txtQty.setText(Global.FloatToStrFmt(mCurrentItem.getQty(), false));

                    totalPesananTemp += mCurrentItem.getHarga();
                    penjualanKeranjangActivity.setTotalPesanan(totalPesananTemp);
                    keranjangMasterTable.updateQtyKeranjang(mCurrentItem.getSeq(), qtyTemp);
                }
            });

            /*itemHolder.txtQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focus) {
                    if (!focus){
                        *//**
                         * PENTING !!!
                         * Jika ada perubahan maka ubah juga di setOnFocusChangeListener atau setOnEditorActionListener
                         * *//*
                        // Ambil nilai qty yang baru diubah
                        int qtyBaru = Integer.parseInt(itemHolder.txtQty.getText().toString());

                        if (qtyBaru == 0){
                            keranjangMasterTable.deleteKeranjang(mCurrentItem.getSeq());
                            penjualanKeranjangActivity.LoadData();
                            return;
                        }

                        // Hitung perbedaan qty baru dengan qty sebelumnya
                        int perbedaanQty = (int) (qtyBaru - mCurrentItem.getQty());

                        // Update qty pada mCurrentItem
                        mCurrentItem.setQty(qtyBaru);


                        // Hitung total pesanan
                        totalPesananTemp += (perbedaanQty * mCurrentItem.getHarga());
                        penjualanKeranjangActivity.setTotalPesanan(totalPesananTemp);
                        keranjangMasterTable.updateQtyKeranjang(mCurrentItem.getSeq(), qtyBaru);

                    }
                }
            });

            itemHolder.txtQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT ||
                            (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        *//**
                         * PENTING !!!
                         * Jika ada perubahan maka ubah juga di setOnFocusChangeListener atau setOnEditorActionListener
                         * *//*
                        // Ambil nilai qty yang baru diubah
                        int qtyBaru = Integer.parseInt(textView.getText().toString());

                        if (qtyBaru == 0){
                            keranjangMasterTable.deleteKeranjang(mCurrentItem.getSeq());
                            penjualanKeranjangActivity.LoadData();
                            return true;
                        }

                        // Hitung perbedaan qty baru dengan qty sebelumnya
                        int perbedaanQty = (int) (qtyBaru - mCurrentItem.getQty());

                        // Update qty pada mCurrentItem
                        mCurrentItem.setQty(qtyBaru);

                        // Hitung total pesanan
                        totalPesananTemp += (perbedaanQty * mCurrentItem.getHarga());
                        penjualanKeranjangActivity.setTotalPesanan(totalPesananTemp);
                        keranjangMasterTable.updateQtyKeranjang(mCurrentItem.getSeq(), qtyBaru);

                        // Sembunyikan keyboard
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        // Hapus fokus dari EditText
                        textView.clearFocus();
                        return true;
                    }
                    return false;
                }
            });*/

            itemHolder.txtQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(itemHolder.getPosition() == position) {
                        if (editable.toString().isEmpty()){
                            return;
                        }
                        // Ambil nilai qty yang baru diubah
                        int qtyBaru = Integer.parseInt(editable.toString());

                        if (qtyBaru == 0) {
                            keranjangMasterTable.deleteKeranjang(mCurrentItem.getSeq());
                            penjualanKeranjangActivity.LoadData();
                            MainActivity.setInitMainActivity(false);
                            return;
                        }

                        // Hitung perbedaan qty baru dengan qty sebelumnya
                        int perbedaanQty = (int) (qtyBaru - mCurrentItem.getQty());

                        // Update qty pada mCurrentItem
                        mCurrentItem.setQty(qtyBaru);


                        // Hitung total pesanan
                        totalPesananTemp += (perbedaanQty * mCurrentItem.getHarga());
                        penjualanKeranjangActivity.setTotalPesanan(totalPesananTemp);
                        keranjangMasterTable.updateQtyKeranjang(mCurrentItem.getSeq(), qtyBaru);
                    }
                }
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        ImageView imgBarang;
        TextView tvNamaBarang, tvHarga, tvEdit, tvMinus, tvPlus, txtQty;

        public ItemHolder(View itemView) {
            super(itemView);
            imgBarang = itemView.findViewById(R.id.imgBarang);
            tvNamaBarang = itemView.findViewById(R.id.tvNamaBarang);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvEdit = itemView.findViewById(R.id.tvEdit);
            tvMinus = itemView.findViewById(R.id.tvMinus);
            tvPlus = itemView.findViewById(R.id.tvPlus);
            txtQty = itemView.findViewById(R.id.txtQty);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<KeranjangMasterModel> KeranjangMasterModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(KeranjangMasterModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(KeranjangMasterModel KeranjangMasterModel) {
        this.Datas.add(KeranjangMasterModel);
        notifyItemRangeInserted(Datas.size() - 1, Datas.size() - 1);
    }

    public void removeModel(int idx) {
        if (Datas.size() > 0) {
            this.Datas.remove(Datas.size() - 1);
            notifyItemRemoved(Datas.size());
            notifyItemRangeChanged(idx, Datas.size());
        }
    }

    public void removeAllModel() {
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        notifyItemRangeRemoved(0, LastPosition);
    }

    public double getTotalPesananTemp() {
        return totalPesananTemp;
    }

    public void setTotalPesananTemp(double totalPesananTemp) {
        this.totalPesananTemp = totalPesananTemp;
    }
}
