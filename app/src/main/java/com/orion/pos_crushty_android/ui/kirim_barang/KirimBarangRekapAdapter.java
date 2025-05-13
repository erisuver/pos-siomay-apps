package com.orion.pos_crushty_android.ui.kirim_barang;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.kirim_barang.KirimBarangMstModel;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.ui.terima.TerimaInputActivity;

import java.util.List;

public class KirimBarangRekapAdapter extends RecyclerView.Adapter {
    Context context;
    List<KirimBarangMstModel> Datas;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    private int ViewAlamat;

    public KirimBarangRekapAdapter(Context context, List<KirimBarangMstModel> Datas, int view) {
        this.context = context;
        this.Datas = Datas;
        this.Loading = new ProgressDialog(context);
        this.ViewAlamat = view;
    }
    public void addModels(List<KirimBarangMstModel> Datas) {
        int pos = this.Datas.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
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
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(ViewAlamat, parent, false);
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
            final KirimBarangMstModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.tvNomor.setText(mCurrentItem.getNomor());
            itemHolder.tvTanggal.setText(mCurrentItem.getTanggal());
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), KirimBarangInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    intent.putExtra("master_uid", mCurrentItem.getUid());
                    intent.putExtra("nomor", mCurrentItem.getNomor());
                    intent.putExtra("keterangan", mCurrentItem.getKeterangan());
                    view.getContext().startActivity(intent);
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
        TextView tvTanggal, tvNomor;
        public ItemHolder(View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvNomor = itemView.findViewById(R.id.tvNomor);
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
