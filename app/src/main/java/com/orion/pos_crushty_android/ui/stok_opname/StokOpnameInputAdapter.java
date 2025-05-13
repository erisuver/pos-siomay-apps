package com.orion.pos_crushty_android.ui.stok_opname;

import static android.view.View.GONE;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.databases.stok_opname_det.StokOpnameDetModel;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.ui.laporan_penjualan.LaporanPenjualanActivity;
import com.orion.pos_crushty_android.ui.lov.LovBarangActivity;
import com.orion.pos_crushty_android.utility.TextWatcherUtils;

import java.util.List;
import java.util.Locale;

public class StokOpnameInputAdapter extends RecyclerView.Adapter {
    Context context;
    List<StokOpnameDetModel> Datas;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    int view;
    final int REQUEST_FILTER_BARANG = 1;
    private String Is_Awal = "T";
    StokOpnameInputActivity stokOpnameInputActivity;
    private AdapterView.OnItemClickListener listener;

    public StokOpnameInputAdapter(StokOpnameInputActivity stokOpnameInputActivity, List<StokOpnameDetModel> Datas, int view) {
        this.context = stokOpnameInputActivity.getApplicationContext();
        this.Datas = Datas;
        this.view = view; // This was missing
        this.listener = listener; // Save the listener
        this.stokOpnameInputActivity = stokOpnameInputActivity;
        this.Loading = new ProgressDialog(context);
    }
    public void addModels(List<StokOpnameDetModel> Datas) {
        int pos = this.Datas.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
    }
    public void removeModel(int idx) {
        if (Datas.size() > 0){
            this.Datas.remove(idx);
            notifyItemRemoved(idx);
            notifyItemRangeChanged(idx, Datas.size());
        }
    }
    public void addModel(StokOpnameDetModel Datas) {
        int pos = this.Datas.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
//        this.dataFiltered.add(Datas);
//        notifyItemRangeInserted(pos, dataFiltered.size());
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
            View row = inflater.inflate(view, parent, false);
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
            final StokOpnameDetModel mCurrentItem = Datas.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.txtBarang.setText(mCurrentItem.getNama_brg());
            itemHolder.txtSatuan.setText(mCurrentItem.getSatuan());
            itemHolder.txtStok.setText(Global.FloatToStrFmt2(mCurrentItem.getQty()));
            itemHolder.txtStokProg.setText(Global.FloatToStrFmt2(mCurrentItem.getqty_program()));
            itemHolder.txtSelisih.setText(Global.FloatToStrFmt2(mCurrentItem.getqty_selisih()));
            itemHolder.txtCatatan.setText(mCurrentItem.getKeterangan());

            Global.setEnabledClickText(itemHolder.txtBarang, false);
            if (mCurrentItem.getIs_detail().equals("T")) {
                itemHolder.txtStok.setEnabled(false);
                itemHolder.txtCatatan.setEnabled(false);
                itemHolder.btnremove.setVisibility(GONE);
                itemHolder.btnLovBrg.setVisibility(GONE);
            }

            itemHolder.txtCatatan.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(itemHolder.getPosition() == position) {
                        mCurrentItem.setKeterangan(itemHolder.txtCatatan.getText().toString());
                        //set dirty karena udah diisi
                        stokOpnameInputActivity.setDirty(true);
                    }
                }
            });

            itemHolder.txtStokProg.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(itemHolder.getPosition() == position) {
                        String stokString = itemHolder.txtStok.getText().toString();
                        String stokProgString = editable.toString();
                        // Implementasi khusus di sini
                        double stok = Global.StrFmtToFloat(stokString);
                        double stokProg = Global.StrFmtToFloat(stokProgString);

                        // Calculate the difference and update txtSelisih
                        double selisih = stok - stokProg;
                        mCurrentItem.setQty(stok);
                        if (selisih != 0) {
                            mCurrentItem.setqty_selisih(selisih);
                            itemHolder.txtSelisih.setText(Global.FloatToStrFmt2(selisih));
                        } else {
                            mCurrentItem.setqty_selisih(0);
                            itemHolder.txtSelisih.setText(Global.FloatToStrFmt2(0.0));
                        }

                        //set dirty karena udah diisi
                        stokOpnameInputActivity.setDirty(true);
                    }
                }
            });

            itemHolder.txtStok.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //itemHolder.txtStok.selectAll();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(itemHolder.getPosition() == position) {
                        String stokString = editable.toString();
                        String stokProgString = itemHolder.txtStokProg.getText().toString();
                        // Implementasi khusus di sini
                        double stok = Global.StrFmtToFloat(stokString);
                        double stokProg = Global.StrFmtToFloat(stokProgString);

                        // Calculate the difference and update txtSelisih
                        double selisih = stok - stokProg;
                        mCurrentItem.setQty(stok);
                        if (selisih != 0) {
                            mCurrentItem.setqty_selisih(selisih);
                            itemHolder.txtSelisih.setText(Global.FloatToStrFmt2(selisih));
                        } else {
                            mCurrentItem.setqty_selisih(0);
                            itemHolder.txtSelisih.setText(Global.FloatToStrFmt2(0.0));
                        }
                        //set dirty karena udah diisi
                        stokOpnameInputActivity.setDirty(true);
                    }
                }
            });

            // Set the TextWatcher to txtStok
//            itemHolder.txtStok.addTextChangedListener(textWatcher);
//            itemHolder.txtStok.setTag(textWatcher); // Keep a reference to remove it if neede

            itemHolder.btnremove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeModel(position);
                }
            });
            /**Ditutup karena sudah tidak menambahkan satu2 alias langsung muncul semua barang saat input.
             *
            itemHolder.btnLovBrg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemHolder.itemView.clearFocus();
                    Intent intent = new Intent(context, LovBarangActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("isMunculNonStok", false);
                    stokOpnameInputActivity.startActivityForResult(intent, REQUEST_FILTER_BARANG);
                    stokOpnameInputActivity.adapterPos = position;
                    //listener.onLovBarangClick(holder.getAdapterPosition());

                }
            });
//            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            itemHolder.txtBarang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemHolder.itemView.clearFocus();
                    Intent intent = new Intent(context, LovBarangActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    stokOpnameInputActivity.startActivityForResult(intent, REQUEST_FILTER_BARANG);
                    stokOpnameInputActivity.adapterPos = position;
                    //listener.onLovBarangClick(holder.getAdapterPosition());
                }
            });
            */

        }else if (holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    public interface OnItemClickListener {
        void onLovBarangClick(int position);
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
//        EditText txtBarang;
        TextInputEditText txtStok, txtStokProg, txtSelisih, txtCatatan, txtBarang;
        TextView txtSatuan;
        ImageButton btnLovBrg, btnremove;
        View lineView;
        LinearLayout llbutton;
        TextInputLayout tilBarang;
        public ItemHolder(View itemView) {
            super(itemView);
            txtBarang = itemView.findViewById(R.id.txtNamaBrg);
            txtStok =  itemView.findViewById(R.id.txtStokReal);
            txtStokProg =  itemView.findViewById(R.id.txtStokProgram);
            txtSelisih =  itemView.findViewById(R.id.txtSelisih);
            txtCatatan =  itemView.findViewById(R.id.txtCatatan);
            txtSatuan = itemView.findViewById(R.id.txtSatuan);
            btnLovBrg = itemView.findViewById(R.id.btnItemBrg);
            btnremove = itemView.findViewById(R.id.btnRemoveItem);
            lineView = itemView.findViewById(R.id.Lineview);
            llbutton = itemView.findViewById(R.id.LLButtonAdd);
            tilBarang = itemView.findViewById(R.id.tilBarang);
//            txtStok.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
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
