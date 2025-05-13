package com.orion.pos_crushty_android.ui.cetak_struk;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.globals.JConst;

import java.util.List;

public class CetakStrukAdapter extends RecyclerView.Adapter {
    Context context;
    List<CetakStrukModel> Datas;
    CetakStrukActivity cetakStrukActivity;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public CetakStrukAdapter(CetakStrukActivity cetakStrukActivity, List<CetakStrukModel> cetakStrukModel, int view) {
        this.context = cetakStrukActivity.getApplicationContext();
        this.cetakStrukActivity = cetakStrukActivity;
        this.Datas = cetakStrukModel;
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
            final CetakStrukModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.txtIsi.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            itemHolder.txtIsi.setText(mCurrentItem.getIsi());

            /*// Jika teks adalah "CRUSHTY", atur ukuran font lebih besar
            // 1️⃣ Trim teks dulu supaya bersih
            String teksTrimmed = mCurrentItem.getIsi().trim();

            // 2️⃣ Besarkan ukuran teks kalau "CRUSHTY"
            if (teksTrimmed.contains("CRUSHTY")) {
                itemHolder.txtIsi.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                itemHolder.txtIsi.setTypeface(Typeface.MONOSPACE, Typeface.BOLD); // Bisa ditambah bold biar makin jelas
            } else {
                itemHolder.txtIsi.setTextSize(10); // Ukuran normal
                itemHolder.txtIsi.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            }*/

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
        TextView txtIsi;

        public ItemHolder(View itemView) {
            super(itemView);
            txtIsi = itemView.findViewById(R.id.txtIsi);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<CetakStrukModel> CetakStrukModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(CetakStrukModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(CetakStrukModel CetakStrukModel) {
        this.Datas.add(CetakStrukModel);
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
