package com.orion.pos_crushty_android.ui.shift_kasir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.shift_master.ShiftMasterModel;
import com.orion.pos_crushty_android.globals.Global;

import java.util.List;

public class RiwayatShiftAdapter extends RecyclerView.Adapter {
    Context context;
    List<ShiftMasterModel> Datas;
    RiwayatShiftFragment riwayatShiftFragment;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public RiwayatShiftAdapter(RiwayatShiftFragment riwayatShiftFragment, List<ShiftMasterModel> shiftMasterModel, int view) {
        this.context = riwayatShiftFragment.getContext();
        this.riwayatShiftFragment = riwayatShiftFragment;
        this.Datas = shiftMasterModel;
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
            final ShiftMasterModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.txtKasir.setText(mCurrentItem.getNamaKasir());
            itemHolder.txtShiftMulai.setText(mCurrentItem.getShiftMulai());
            itemHolder.txtShiftTutup.setText(mCurrentItem.getShiftAkhir());
            itemHolder.txtPenerimaanSistem.setText(Global.FloatToStrFmt(mCurrentItem.getProgramTunai(), true));
            itemHolder.txtPenerimaanAktual.setText(Global.FloatToStrFmt(mCurrentItem.getTunaiAktual(), true));
            itemHolder.tvSelisih.setText(Global.FloatToStrFmt(mCurrentItem.getSelisih(), true));

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
        EditText txtKasir, txtShiftMulai, txtShiftTutup, txtPenerimaanSistem, txtPenerimaanAktual;
        TextView tvSelisih;

        public ItemHolder(View itemView) {
            super(itemView);
            txtKasir = itemView.findViewById(R.id.txtKasir);
            txtShiftMulai = itemView.findViewById(R.id.txtShiftMulai);
            txtShiftTutup = itemView.findViewById(R.id.txtShiftTutup);
            txtPenerimaanSistem = itemView.findViewById(R.id.txtPenerimaanSistem);
            txtPenerimaanAktual = itemView.findViewById(R.id.txtPenerimaanAktual);
            tvSelisih = itemView.findViewById(R.id.tvSelisih);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<ShiftMasterModel> ShiftMasterModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(ShiftMasterModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(ShiftMasterModel ShiftMasterModel) {
        this.Datas.add(ShiftMasterModel);
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
}
