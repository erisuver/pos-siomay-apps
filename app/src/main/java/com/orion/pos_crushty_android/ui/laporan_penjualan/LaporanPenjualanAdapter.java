package com.orion.pos_crushty_android.ui.laporan_penjualan;

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
import com.orion.pos_crushty_android.databases.penjualan_mst.PenjualanMstModel;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.ui.cetak_struk.CetakStrukActivity;

import java.util.List;

public class LaporanPenjualanAdapter extends RecyclerView.Adapter {
    Context context;
    List<PenjualanMstModel> Datas;
    LaporanPenjualanActivity laporanPenjualanActivity;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public LaporanPenjualanAdapter(LaporanPenjualanActivity laporanPenjualanActivity, List<PenjualanMstModel> penjualanMstModel, int view) {
        this.context = laporanPenjualanActivity.getApplicationContext();
        this.laporanPenjualanActivity = laporanPenjualanActivity;
        this.Datas = penjualanMstModel;
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
            final PenjualanMstModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.tvNomor.setText(mCurrentItem.getNomor());
            itemHolder.tvTanggal.setText(Global.getDateTimeFormated(mCurrentItem.getTanggal()));
            itemHolder.tvTotal.setText(Global.FloatToStrFmt(mCurrentItem.getTotal(), true));

            itemHolder.viewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(laporanPenjualanActivity, CetakStrukActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("tipe_bayar", mCurrentItem.getTipeBayar());
                    intent.putExtra("penjualan_mst_uid", mCurrentItem.getUid());
                    intent.putExtra("is_laporan", true);
                    laporanPenjualanActivity.startActivity(intent);
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
        TextView tvNomor, tvTanggal, tvTotal;
        View viewItem;

        public ItemHolder(View itemView) {
            super(itemView);
            tvNomor = itemView.findViewById(R.id.tvNomor);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            viewItem = itemView.findViewById(R.id.viewItem);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<PenjualanMstModel> PenjualanMstModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(PenjualanMstModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(PenjualanMstModel PenjualanMstModel) {
        this.Datas.add(PenjualanMstModel);
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
