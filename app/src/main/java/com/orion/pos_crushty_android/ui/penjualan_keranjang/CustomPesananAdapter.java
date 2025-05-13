package com.orion.pos_crushty_android.ui.penjualan_keranjang;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.keranjang_detail.KeranjangDetailModel;
import com.orion.pos_crushty_android.databases.keranjang_detail.KeranjangDetailTable;
import com.orion.pos_crushty_android.globals.Global;

import java.util.List;

public class CustomPesananAdapter extends RecyclerView.Adapter {
    Context context;
    List<KeranjangDetailModel> Datas;
    CustomPesananActivity customPesananActivity;
    KeranjangDetailTable keranjangDetailTable;
    private int view;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;

    public CustomPesananAdapter(CustomPesananActivity customPesananActivity, List<KeranjangDetailModel> keranjangModels, int view) {
        this.context = customPesananActivity.getApplicationContext();
        this.customPesananActivity = customPesananActivity;
        this.Datas = keranjangModels;
        this.view = view;
        this.keranjangDetailTable = new KeranjangDetailTable(context);
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
            final KeranjangDetailModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.tvNamaBarang.setText(mCurrentItem.getNamaBarang());
            itemHolder.txtQty.setText(Global.FloatToStrFmt(mCurrentItem.getQty(), false));

            itemHolder.tvMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qtyTemp = (int) (mCurrentItem.getQty() - 1);
                    mCurrentItem.setQty(qtyTemp);
                    itemHolder.txtQty.setText(Global.FloatToStrFmt(mCurrentItem.getQty(), false));

                }
            });

            itemHolder.tvPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qtyTemp = (int) (mCurrentItem.getQty() + 1);
                    mCurrentItem.setQty(qtyTemp);
                    itemHolder.txtQty.setText(Global.FloatToStrFmt(mCurrentItem.getQty(), false));

                }
            });
/*

            itemHolder.txtQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focus) {
                    if (!focus){
                        */
/**
                         * PENTING !!!
                         * Jika ada perubahan maka ubah juga di setOnFocusChangeListener atau setOnEditorActionListener
                         * *//*

                        // Ambil nilai qty yang baru diubah
                        int qtyBaru = Integer.parseInt(itemHolder.txtQty.getText().toString());

                        // Update qty pada mCurrentItem
                        mCurrentItem.setQty(qtyBaru);

                    }
                }
            });

            itemHolder.txtQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT ||
                            (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        */
/**
                         * PENTING !!!
                         * Jika ada perubahan maka ubah juga di setOnFocusChangeListener atau setOnEditorActionListener
                         * *//*

                        // Ambil nilai qty yang baru diubah
                        int qtyBaru = Integer.parseInt(textView.getText().toString());

                        // Update qty pada mCurrentItem
                        mCurrentItem.setQty(qtyBaru);

                        // Sembunyikan keyboard
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        // Hapus fokus dari EditText
                        textView.clearFocus();
                        return true;
                    }
                    return false;
                }
            });
*/
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
                        if (editable.toString().isEmpty()) {
                            return;
                        }
                        // Ambil nilai qty yang baru diubah
                        int qtyBaru = Integer.parseInt(editable.toString());

                        // Update qty pada mCurrentItem
                        mCurrentItem.setQty(qtyBaru);
                    }
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
        TextView tvNamaBarang, tvMinus, tvPlus, txtQty;

        public ItemHolder(View itemView) {
            super(itemView);
            tvNamaBarang = itemView.findViewById(R.id.tvNamaBarang);
            tvMinus = itemView.findViewById(R.id.tvMinus);
            tvPlus = itemView.findViewById(R.id.tvPlus);
            txtQty = itemView.findViewById(R.id.txtQty);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    public void addModels(List<KeranjangDetailModel> KeranjangDetailModel) {
        int pos = this.Datas.size();
        this.Datas.addAll(KeranjangDetailModel);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void addModel(KeranjangDetailModel KeranjangDetailModel) {
        this.Datas.add(KeranjangDetailModel);
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
