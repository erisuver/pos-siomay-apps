package com.orion.pos_crushty_android.ui.terima;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.terima_barang.TerimaBarangDetModel;
import com.orion.pos_crushty_android.globals.Global;

import java.util.List;

public class TerimaInputAdapter extends RecyclerView.Adapter {
    Context context;
    List<TerimaBarangDetModel> Datas;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    int view;
    private String Is_Awal = "T";
    TerimaInputActivity terimaInputActivity;
    private AdapterView.OnItemClickListener listener;

    public TerimaInputAdapter(TerimaInputActivity terimaInputActivity, List<TerimaBarangDetModel> Datas, int view) {
        this.context = terimaInputActivity.getApplicationContext();
        this.Datas = Datas;
        this.view = view; // This was missing
        this.listener = listener; // Save the listener
        this.terimaInputActivity = terimaInputActivity;
        this.Loading = new ProgressDialog(context);
    }
    public void addModels(List<TerimaBarangDetModel> Datas) {
        int pos = this.Datas.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
    }
    public void removeModel(int idx) {
        if (Datas.size() > 0){
            this.Datas.remove(Datas.size()-1);
            notifyItemRemoved(Datas.size());
            notifyItemRangeChanged(idx, Datas.size());
        }
    }
    public void addModel(TerimaBarangDetModel Datas) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            final TerimaBarangDetModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.tvBarang.setText(mCurrentItem.getKodeBarang()+" - "+mCurrentItem.getNamaBarang());
            itemHolder.tvSatuan.setText(mCurrentItem.getNamaSatuan());
            itemHolder.txtKeterangan.setText(mCurrentItem.getKeterangan());
            itemHolder.txtQty.setText(Global.FloatToStrFmt(mCurrentItem.getQty()));
            if (!mCurrentItem.getMasterUid().equals("")){
                itemHolder.txtQty.setEnabled(false);
                itemHolder.txtKeterangan.setEnabled(false);
            }
            // Add a TextWatcher to txtStok to listen for changes
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // No action required before the text is changed
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // No action required during the text change
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };

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
                        mCurrentItem.setQtyPrimer(mCurrentItem.getKonversi() * qty);
                    }
                }
            });
            itemHolder.txtKeterangan.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(itemHolder.getPosition() == position) {
                        mCurrentItem.setKeterangan(editable.toString());
                    }
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
        EditText txtKeterangan, txtQty;
        TextView tvSatuan, tvBarang;
        public ItemHolder(View itemView) {
            super(itemView);
            tvBarang = itemView.findViewById(R.id.tvBarang);
            tvSatuan =  itemView.findViewById(R.id.tvSatuan);
            txtQty =  itemView.findViewById(R.id.txtQtyTerima);
            txtKeterangan =  itemView.findViewById(R.id.txtKeterangan);
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
