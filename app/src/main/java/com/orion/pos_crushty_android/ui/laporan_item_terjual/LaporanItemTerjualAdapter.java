package com.orion.pos_crushty_android.ui.laporan_item_terjual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.penjualan_mst.ItemTerjualModel;
import com.orion.pos_crushty_android.globals.Global;

import java.util.List;

public class LaporanItemTerjualAdapter extends RecyclerView.Adapter {
    Context context;
    List<ItemTerjualModel> Datas;
    LaporanItemTerjualActivity laporanPenjualanProdukActivity;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public LaporanItemTerjualAdapter(LaporanItemTerjualActivity laporanPenjualanProdukActivity, List<ItemTerjualModel> itemTerjualModel, int view) {
        this.context = laporanPenjualanProdukActivity.getApplicationContext();
        this.laporanPenjualanProdukActivity = laporanPenjualanProdukActivity;
        this.Datas = itemTerjualModel;
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
            final ItemTerjualModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.tvNama.setText(mCurrentItem.getNamaBarang());
            itemHolder.tvQty.setText(Global.FloatToStrFmt(mCurrentItem.getQty()));
            itemHolder.tvHarga.setText(Global.FloatToStrFmt(mCurrentItem.getHarga()));
            itemHolder.tvTotal.setText(Global.FloatToStrFmt(mCurrentItem.getTotal()));

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
        TextView tvNama, tvQty, tvHarga, tvTotal;

        public ItemHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<ItemTerjualModel> ItemTerjualModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(ItemTerjualModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(ItemTerjualModel ItemTerjualModel) {
        this.Datas.add(ItemTerjualModel);
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
