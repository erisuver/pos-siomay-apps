package com.orion.pos_crushty_android.ui.pengeluaran_lain;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainDetModel;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.ui.main.fragments.PengeluaranLainKasFragment;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.ui.main.fragments.PengeluaranLainKasFragment;

import java.util.List;

public class PengeluaranLainKasAdapter extends RecyclerView.Adapter{
    Context context;
    List<PengeluaranLainDetModel> Datas;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    int view;
    final int REQUEST_FILTER_BARANG = 1;
    private String Is_Awal = "T";
    PengeluaranLainKasFragment pengeluaranLainKasFragment;
    private AdapterView.OnItemClickListener listener;
    
    public PengeluaranLainKasAdapter(PengeluaranLainKasFragment pengeluaranLainKasFragment, List<PengeluaranLainDetModel> pengeluaranLainDetModel, int view) {
        this.context = pengeluaranLainKasFragment.getContext();
        this.pengeluaranLainKasFragment = pengeluaranLainKasFragment;
        this.Datas = pengeluaranLainDetModel;
        this.view = view;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new PengeluaranLainKasAdapter.ItemHolder(row);
        } else if (viewType == VIEW_TYVE_LOADING) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new PengeluaranLainKasAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PengeluaranLainKasAdapter.ItemHolder) {
            final PengeluaranLainDetModel mCurrentItem = Datas.get(position);
            final PengeluaranLainKasAdapter.ItemHolder itemHolder = (PengeluaranLainKasAdapter.ItemHolder) holder;
            itemHolder.txtKeterangan.setText(mCurrentItem.getKeterangan());
            itemHolder.txtJumlah.setText(Global.FloatToStrFmt(mCurrentItem.getJumlah(),  true));
            itemHolder.txtJumlah.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    mCurrentItem.setJumlah(Global.StrFmtToFloatInput(editable.toString()));
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
                    mCurrentItem.setKeterangan(editable.toString());
                }
            });

        } else if (holder instanceof PengeluaranLainKasAdapter.LoadingViewHolder) {
            PengeluaranLainKasAdapter.LoadingViewHolder loadingViewHolder = (PengeluaranLainKasAdapter.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        EditText txtKeterangan, txtJumlah;

        public ItemHolder(View itemView) {
            super(itemView);
            txtKeterangan = itemView.findViewById(R.id.txtKeteranganPLK);
            txtJumlah = itemView.findViewById(R.id.txtJumlah);
        }
    }
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<PengeluaranLainDetModel> PengeluaranLainDetModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(PengeluaranLainDetModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(PengeluaranLainDetModel PengeluaranLainDetModel) {
        this.Datas.add(PengeluaranLainDetModel);
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
