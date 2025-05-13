package com.orion.pos_crushty_android.ui.pengeluaran_lain;

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
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstModel;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.ui.cetak_struk.CetakStrukActivity;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.PengeluaranLainRekapActivity;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.PengeluaranLainRekapAdapter;
import com.orion.pos_crushty_android.ui.stok_opname.StokOpnameInputActivity;

import java.util.List;

public class PengeluaranLainRekapAdapter extends RecyclerView.Adapter {
    Context context;
    List<PengeluaranLainMstModel> Datas;
    PengeluaranLainRekapActivity pengeluaranLainRekapActivity;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public PengeluaranLainRekapAdapter(PengeluaranLainRekapActivity pengeluaranLainRekapActivity, List<PengeluaranLainMstModel> PengeluaranLainMstModel, int view) {
        this.context = pengeluaranLainRekapActivity.getApplicationContext();
        this.pengeluaranLainRekapActivity = pengeluaranLainRekapActivity;
        this.Datas = PengeluaranLainMstModel;
        this.view = view;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new PengeluaranLainRekapAdapter.ItemHolder(row);
        } else if (viewType == VIEW_TYVE_LOADING) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new PengeluaranLainRekapAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PengeluaranLainRekapAdapter.ItemHolder) {
            final PengeluaranLainMstModel mCurrentItem = Datas.get(position);
            final PengeluaranLainRekapAdapter.ItemHolder itemHolder = (PengeluaranLainRekapAdapter.ItemHolder) holder;

            itemHolder.tvNomor.setText(mCurrentItem.getNomor());
            itemHolder.tvTanggal.setText(FungsiGeneral.getTglFmt(mCurrentItem.getTanggal(), "dd/MM/yyyy HH:mm"));

            itemHolder.viewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent s = new Intent(pengeluaranLainRekapActivity, PengeluaranLainInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    s.putExtra("outlet_uid", mCurrentItem.getOutletUid());
                    s.putExtra("uid", mCurrentItem.getUid());
                    s.putExtra("nomor", mCurrentItem.getNomor());
                    s.putExtra("keterangan", mCurrentItem.getKeterangan());
                    s.putExtra("is_detail", "T");
                    s.putExtra("tipe", mCurrentItem.getTipe());
                    pengeluaranLainRekapActivity.startActivity(s);
                }
            });

        } else if (holder instanceof PengeluaranLainRekapAdapter.LoadingViewHolder) {
            PengeluaranLainRekapAdapter.LoadingViewHolder loadingViewHolder = (PengeluaranLainRekapAdapter.LoadingViewHolder) holder;
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

    public void addModels(List<PengeluaranLainMstModel> PengeluaranLainMstModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(PengeluaranLainMstModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(PengeluaranLainMstModel PengeluaranLainMstModel) {
        this.Datas.add(PengeluaranLainMstModel);
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
