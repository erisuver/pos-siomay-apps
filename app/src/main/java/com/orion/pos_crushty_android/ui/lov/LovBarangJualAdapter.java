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
import com.orion.pos_crushty_android.databases.master_barang_jual.MasterBarangJualModel;

import java.util.List;

public class LovBarangJualAdapter extends RecyclerView.Adapter {
    Context context;
    List<MasterBarangJualModel> Datas;
    LovBarangJualActivity lovBarangJualActivity;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public LovBarangJualAdapter(LovBarangJualActivity lovBarangJualActivity, List<MasterBarangJualModel> masterBarangJualModel, int view) {
        this.context = lovBarangJualActivity.getApplicationContext();
        this.lovBarangJualActivity = lovBarangJualActivity;
        this.Datas = masterBarangJualModel;
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
            final MasterBarangJualModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.tvKode.setText(mCurrentItem.getKode());
            itemHolder.tvNama.setText(mCurrentItem.getNama());
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = lovBarangJualActivity.getIntent();
                    intent.putExtra("barang_jual_uid", mCurrentItem.getUid());
                    lovBarangJualActivity.setResult(RESULT_OK, intent);
                    lovBarangJualActivity.finish();
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

    public void addModels(List<MasterBarangJualModel> MasterBarangJualModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(MasterBarangJualModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(MasterBarangJualModel MasterBarangJualModel) {
        this.Datas.add(MasterBarangJualModel);
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
