package com.orion.pos_crushty_android.ui.stok_opname;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.stok_opname_mst.StokOpnameMstModel;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.ui.lov.LovBarangActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class StokOpnameRekapAdapter extends RecyclerView.Adapter {
    Context context;
    List<StokOpnameMstModel> Datas;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    private int ViewAlamat;

    final int REQUEST_DETAIl = 2;
    StokOpnameRekapActivity stokOpnameRekapActivity;
    public StokOpnameRekapAdapter(Context context, List<StokOpnameMstModel> Datas, int view) {
        this.context = context;
        this.Datas = Datas;
        this.Loading = new ProgressDialog(context);
        this.ViewAlamat = view;
    }
    public void addModels(List<StokOpnameMstModel> Datas) {
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
            final StokOpnameMstModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.txtNomor.setText(mCurrentItem.getNomor());
            itemHolder.txtTanggal.setText(FungsiGeneral.getTglFmt(mCurrentItem.getTanggal(), "dd/MM/yyyy HH:mm"));
            itemHolder.cvDetailSO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent s = new Intent(context, StokOpnameInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    s.putExtra("outlet_uid", mCurrentItem.getOutletUid());
                    s.putExtra("uid", mCurrentItem.getUid());
                    s.putExtra("nomor", mCurrentItem.getNomor());
                    s.putExtra("keterangan", mCurrentItem.getKeterangan());
                    s.putExtra("is_detail", "T");
                    context.startActivity(s);
//                    stokOpnameRekapActivity.startActivityForResult(s, REQUEST_DETAIl);
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
        TextView txtTanggal, txtNomor;
        ImageButton btnDet;
        CardView cvDetailSO;
        public ItemHolder(View itemView) {
            super(itemView);
            txtTanggal = itemView.findViewById(R.id.txtTanggal);
            txtNomor = itemView.findViewById(R.id.txtNomor);
            btnDet = itemView.findViewById(R.id.btnAction22);
            cvDetailSO = itemView.findViewById(R.id.cvDetailSO);
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
