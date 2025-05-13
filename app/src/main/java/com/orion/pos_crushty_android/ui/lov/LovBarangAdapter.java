package com.orion.pos_crushty_android.ui.lov;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.master_barang.MasterBarangModel;
import com.orion.pos_crushty_android.globals.Global;

import java.util.List;

public class LovBarangAdapter extends RecyclerView.Adapter {
    Context context;
    List<MasterBarangModel> Datas;
    LovBarangActivity lovBarangActivity;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public LovBarangAdapter(LovBarangActivity lovBarangActivity, List<MasterBarangModel> masterBarangModel, int view) {
        this.context = lovBarangActivity.getApplicationContext();
        this.lovBarangActivity = lovBarangActivity;
        this.Datas = masterBarangModel;
        this.view = view;
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
            final MasterBarangModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.tvKode.setText(mCurrentItem.getKode());
            itemHolder.tvNama.setText(mCurrentItem.getNama());
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = lovBarangActivity.getIntent();
                    double konversi = mCurrentItem.getQtyPrimer();
                    intent.putExtra("barang_uid", mCurrentItem.getUid());
                    intent.putExtra("barang_nama", mCurrentItem.getNama());
                    intent.putExtra("satuan_uid", mCurrentItem.getSatuanPrimerUid());
                    intent.putExtra("konversi_primer", konversi);
                    intent.putExtra("satuan_primer_uid", mCurrentItem.getSatuanPrimerUid());
                    intent.putExtra("nama_satuan_primer", mCurrentItem.getNamaSatuanPrimer());
                    lovBarangActivity.setResult(RESULT_OK, intent);
                    lovBarangActivity.finish();
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
        TextView tvNama, tvKode;

        public ItemHolder(View itemView) {
            super(itemView);
            tvKode = itemView.findViewById(R.id.tvKode);
            tvNama = itemView.findViewById(R.id.tvNama);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<MasterBarangModel> MasterBarangModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(MasterBarangModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(MasterBarangModel MasterBarangModel) {
        this.Datas.add(MasterBarangModel);
        notifyItemRangeInserted(Datas.size() - 1, Datas.size() - 1);
    }

    public void removeModel(int idx) {
        if (Datas.size() > 0) {
            this.Datas.remove(Datas.size() - 1);
            notifyItemRemoved(idx);
            notifyItemRangeChanged(idx, Datas.size());
        }
    }

    public void removeAllModel() {
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        notifyItemRangeRemoved(0, LastPosition);
    }
}
