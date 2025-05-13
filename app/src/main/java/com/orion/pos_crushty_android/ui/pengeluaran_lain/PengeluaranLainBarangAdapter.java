package com.orion.pos_crushty_android.ui.pengeluaran_lain;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainDetModel;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.ui.lov.LovBarangActivity;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.ui.main.fragments.PengeluaranLainBarangFragment;
import com.orion.pos_crushty_android.utility.TextWatcherUtils;

import java.util.List;

public class PengeluaranLainBarangAdapter extends RecyclerView.Adapter {
    Context context;
    public List<PengeluaranLainDetModel> Datas;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    int view;
    final int REQUEST_FILTER_BARANG = 1;
    private String Is_Awal = "T";
    PengeluaranLainBarangFragment pengeluaranLainBarangFragment;
    private AdapterView.OnItemClickListener listener;

    public PengeluaranLainBarangAdapter(PengeluaranLainBarangFragment pengeluaranLainBarangFragment, List<PengeluaranLainDetModel> pengeluaranLainDetModel, int view) {
        this.context = pengeluaranLainBarangFragment.getContext();
        this.pengeluaranLainBarangFragment = pengeluaranLainBarangFragment;
        this.Datas = pengeluaranLainDetModel;
        this.view = view;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new PengeluaranLainBarangAdapter.ItemHolder(row);
        } else if (viewType == VIEW_TYVE_LOADING) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new PengeluaranLainBarangAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof PengeluaranLainBarangAdapter.ItemHolder) {
            final PengeluaranLainDetModel mCurrentItem = Datas.get(position);
            final PengeluaranLainBarangAdapter.ItemHolder itemHolder = (PengeluaranLainBarangAdapter.ItemHolder) holder;

            Global.setEnabledClickText(itemHolder.txtBarang, false);
            if (pengeluaranLainBarangFragment.mode.equals("detail")) {
                itemHolder.txtQty.setEnabled(false);
                itemHolder.txtBarang.setEnabled(false);
                itemHolder.btnItemBrg.setVisibility(View.GONE);
                itemHolder.btnremove.setVisibility(View.GONE);
            }

            itemHolder.txtBarang.setText(mCurrentItem.getBarang());
            itemHolder.txtQty.setText(Global.FloatToStrFmt(mCurrentItem.getQty()));
            itemHolder.tvSatuan.setText(mCurrentItem.getSatuan());

            itemHolder.btnremove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeModel(position);
                    notifyDataSetChanged();
                }
            });

            itemHolder.txtBarang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemHolder.itemView.clearFocus();
                    Intent intent = new Intent(context, LovBarangActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    pengeluaranLainBarangFragment.startActivityForResult(intent, REQUEST_FILTER_BARANG);
                    pengeluaranLainBarangFragment.adapterPos = position;
                }
            });
            itemHolder.btnItemBrg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemHolder.itemView.clearFocus();
                    Intent intent = new Intent(context, LovBarangActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    pengeluaranLainBarangFragment.startActivityForResult(intent, REQUEST_FILTER_BARANG);
                    pengeluaranLainBarangFragment.adapterPos = position;
                }
            });

            itemHolder.txtQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(itemHolder.getPosition() == position) {
                        double qty = 0;
                        String qtyString = editable.toString();
                        qty = Global.StrFmtToFloatInput(qtyString);
                        mCurrentItem.setQty(qty);
                        mCurrentItem.setQtyPrimer(mCurrentItem.getKonversi() * qty);
                    }
                }
            });

        } else if (holder instanceof PengeluaranLainBarangAdapter.LoadingViewHolder) {
            PengeluaranLainBarangAdapter.LoadingViewHolder loadingViewHolder = (PengeluaranLainBarangAdapter.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextInputEditText txtBarang, txtQty;
        ImageButton btnremove, btnItemBrg;
        TextView tvSatuan;

        public ItemHolder(View itemView) {
            super(itemView);
            txtBarang = itemView.findViewById(R.id.txtNamaBrgPL);
            txtQty = itemView.findViewById(R.id.txtQtyPL);
            btnremove = itemView.findViewById(R.id.btnRemoveItem);
            btnItemBrg = itemView.findViewById(R.id.btnItemBrg);
            tvSatuan = itemView.findViewById(R.id.tvSatuan);
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
            this.Datas.remove(idx);
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
