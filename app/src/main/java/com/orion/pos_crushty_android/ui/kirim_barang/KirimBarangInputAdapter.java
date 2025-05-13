package com.orion.pos_crushty_android.ui.kirim_barang;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.kirim_barang.KirimBarangDetModel;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.ui.lov.LovBarangActivity;
import com.orion.pos_crushty_android.ui.kirim_barang.KirimBarangInputActivity;
import com.orion.pos_crushty_android.utility.TextWatcherUtils;

import java.util.List;

public class KirimBarangInputAdapter extends RecyclerView.Adapter {
    Context context;
    List<KirimBarangDetModel> Datas;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    int view;
    final int REQUEST_FILTER_BARANG = 1;
    private String Is_Awal = "T";
    KirimBarangInputActivity kirimBarangInputActivity;
    private AdapterView.OnItemClickListener listener;

    public KirimBarangInputAdapter(KirimBarangInputActivity kirimBarangInputActivity, List<KirimBarangDetModel> Datas, int view) {
        this.context = kirimBarangInputActivity.getApplicationContext();
        this.Datas = Datas;
        this.view = view; // This was missing
        this.listener = listener; // Save the listener
        this.kirimBarangInputActivity = kirimBarangInputActivity;
        this.Loading = new ProgressDialog(context);
    }
    public void addModels(List<KirimBarangDetModel> Datas) {
        int pos = this.Datas.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
    }
    public void removeModel(int idx) {
        if (Datas.size() > 0){
            this.Datas.remove(idx);
            notifyItemRemoved(idx);
            notifyItemRangeChanged(idx, Datas.size());
        }
    }
    public void addModel(KirimBarangDetModel Datas) {
        int pos = this.Datas.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
    }
    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        notifyItemRangeRemoved(0, LastPosition);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ItemHolder) {
            final KirimBarangDetModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.txtBarang.setText(mCurrentItem.getNamaBarang());
            itemHolder.tvSatuan.setText(mCurrentItem.getNamaSatuan());
            itemHolder.txtKetDet.setText(mCurrentItem.getKeterangan());
            itemHolder.txtQty.setText(Global.FloatToStrFmt(mCurrentItem.getQty()));
            itemHolder.btnremove.setEnabled(true);

            Global.setEnabledClickText(itemHolder.txtBarang, false);
            if (!mCurrentItem.getMasterUid().equals("")){
               itemHolder.btnremove.setVisibility(View.GONE);
               itemHolder.btnLovBrg.setVisibility(View.GONE);
               itemHolder.txtQty.setEnabled(false);
                itemHolder.txtBarang.setEnabled(false);
                itemHolder.txtKetDet.setEnabled(false);
            }

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
                        double qty = 0;
                        String qtyString = editable.toString();
                        qty = Global.StrFmtToFloatInput(qtyString);
                        mCurrentItem.setQty(qty);
                        mCurrentItem.setQtyPrimer(mCurrentItem.getKonversi()*qty);
                    }

                }
            });
            itemHolder.txtKetDet.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    mCurrentItem.setKeterangan(editable.toString());
                }
            });

            itemHolder.btnremove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemHolder.btnremove.setEnabled(false);
                    removeModel(position);
                }
            });
            itemHolder.btnLovBrg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemHolder.itemView.clearFocus();
                    Intent intent = new Intent(context, LovBarangActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    kirimBarangInputActivity.startActivityForResult(intent, REQUEST_FILTER_BARANG);
                    kirimBarangInputActivity.adapterPos = position;
                    itemHolder.txtBarang.requestFocus();
                }
            });
            itemHolder.txtBarang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemHolder.itemView.clearFocus();
                    Intent intent = new Intent(context, LovBarangActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    kirimBarangInputActivity.startActivityForResult(intent, REQUEST_FILTER_BARANG);
                    kirimBarangInputActivity.adapterPos = position;
                    itemHolder.txtBarang.requestFocus();
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
        TextInputEditText txtBarang, txtQty, txtKetDet;
        TextView tvSatuan;
        ImageButton btnLovBrg, btnremove;
        View lineView;
        LinearLayout llbutton;

        public ItemHolder(View itemView) {
            super(itemView);
            txtBarang = itemView.findViewById(R.id.txtNamaBrg);
            txtQty =  itemView.findViewById(R.id.txtQty);
            txtKetDet =  itemView.findViewById(R.id.txtKeterangan);
            tvSatuan = itemView.findViewById(R.id.tvSatuan);
            btnLovBrg = itemView.findViewById(R.id.btnItemBrg);
            btnremove = itemView.findViewById(R.id.btnRemoveItem);
            lineView = itemView.findViewById(R.id.Lineview);
            llbutton = itemView.findViewById(R.id.LLButtonAdd);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

}
