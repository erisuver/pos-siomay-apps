package com.orion.pos_crushty_android.ui.terima;

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

import com.google.android.material.snackbar.Snackbar;
import com.orion.pos_crushty_android.MainActivity;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.ui.kirim_barang.KirimBarangRekapActivity;
import com.orion.pos_crushty_android.ui.terima.TerimaFragment;
import com.orion.pos_crushty_android.databases.terima_barang.TerimaBarangMstModel;

import java.util.List;

public class TerimaAdapter extends RecyclerView.Adapter {
    Context context;
    List<TerimaBarangMstModel> Datas;
    TerimaFragment terimaFragment;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    final int REQUEST_INPUT  = 2;

    public TerimaAdapter(TerimaFragment terimaFragment, List<TerimaBarangMstModel> terimaBarangMstModel, int view) {
        this.context = terimaFragment.getContext();
        this.terimaFragment = terimaFragment;
        this.Datas = terimaBarangMstModel;
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
            final TerimaBarangMstModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.txtTglKirim.setText(mCurrentItem.getTanggal_kirim());
            itemHolder.txtNomorKirim.setText(mCurrentItem.getNomor_kirim());
            if (mCurrentItem.getSeq().equals(0)){
                itemHolder.txtStatus.setText("Belum Diterima");
                itemHolder.txtStatus.setBackgroundResource(R.drawable.style_status_diterima);
            }else{
                itemHolder.txtStatus.setText("Diterima");
                itemHolder.txtStatus.setBackgroundResource(R.drawable.style_status_belum_diterima);
            }
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Global.checkShift(terimaFragment.getActivity(), () -> {
                        Intent intent = new Intent(view.getContext(), TerimaInputActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        intent.putExtra("master_uid", mCurrentItem.getUid());
                        intent.putExtra("nomor_kirim", mCurrentItem.getNomor_kirim());
                        intent.putExtra("kirim_uid", mCurrentItem.getKirim_uid());
                        intent.putExtra("outlet_asal_uid", mCurrentItem.getOutlet_asal_uid());
                        if (mCurrentItem.getSeq().equals(0)){
                            intent.putExtra("status_terima", JConst.FALSE_STRING);
                        }else{
                            intent.putExtra("status_terima", JConst.TRUE_STRING);
                        }

                        terimaFragment.getActivity().startActivityForResult(intent, JConst.REQUEST_LOAD_TERIMA_BARANG);
//                        view.getContext().startActivity(intent);
                    });
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
        TextView txtTglKirim;
        TextView txtNomorKirim;
        TextView txtStatus;

        public ItemHolder(View itemView) {
            super(itemView);
            txtTglKirim = itemView.findViewById(R.id.txtTglKirim);
            txtNomorKirim = itemView.findViewById(R.id.txtNomorKirim);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<TerimaBarangMstModel> TerimaBarangMstModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(TerimaBarangMstModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(TerimaBarangMstModel TerimaBarangMstModel) {
        this.Datas.add(TerimaBarangMstModel);
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
