package com.orion.pos_crushty_android.ui.pengeluaran_lain.ui.main.fragments;

import static android.app.Activity.RESULT_OK;
import static com.orion.pos_crushty_android.globals.JConst.TIPE_BAYAR_TUNAI;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
import com.orion.pos_crushty_android.Routes;
import com.orion.pos_crushty_android.databases.penambah_pengurang_barang.PenambahPengurangBarangModel;
import com.orion.pos_crushty_android.databases.penambah_pengurang_barang.PenambahPengurangBarangTable;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainDetModel;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainDetTable;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstModel;
import com.orion.pos_crushty_android.databases.pengeluaran_lain.PengeluaranLainMstTable;
import com.orion.pos_crushty_android.globals.FungsiGeneral;
import com.orion.pos_crushty_android.globals.Global;
import com.orion.pos_crushty_android.globals.GlobalTable;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.globals.SharedPrefsUtils;
import com.orion.pos_crushty_android.globals.ShowDialog;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.PengeluaranLainBarangAdapter;
import com.orion.pos_crushty_android.ui.pengeluaran_lain.PengeluaranLainInputActivity;
import com.orion.pos_crushty_android.utility.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PengeluaranLainBarangFragment extends Fragment {
    private RecyclerView rcvLoad;
    private View v;
    private Activity thisActivity;
    public PengeluaranLainBarangAdapter mAdapter;
    private TextView txtNomor, toolbarTitle;
    private Button btnSave;
    public List<PengeluaranLainDetModel> ListItems = new ArrayList<>();
    private Toolbar toolbar;
    public String mode = "";
    final int REQUEST_FILTER_BARANG = 1;
    public int adapterPos;
    private String master_uid, nomor, outletUid, userId ;
    private EditText txtKeterangan;
    private String namaBrg, namaSatuan;
    private String uidBrg, uidSatuan;
    private double stokProgram, konversi;
    ImageButton btnAdd;
    private String  karyawanUid, kodeOutlet, TipeInput, kasbankUid, uuid, Nomor ;
    PengeluaranLainInputActivity pengeluaranLainInputActivity;
    View mainView;
    private PengeluaranLainDetTable pengeluaranLainDetTable;
    private PengeluaranLainMstTable pengeluaranLainMstTable;
    private PenambahPengurangBarangTable penambahPengurangBarangTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barang, container, false);
        v = view;
        JApplication.currentActivity = getActivity();

        CreateView();
        InitClass();
        EventClass();

        if (pengeluaranLainInputActivity.mode != null){
            mode = pengeluaranLainInputActivity.mode;
        }
        if (mode.equals("detail")) {
            LoadData();
            btnSave.setVisibility(View.GONE);
            txtKeterangan.setEnabled(false);
            btnAdd.setVisibility(View.GONE);

            if (!pengeluaranLainInputActivity.tipeInputMst.equals(TipeInput)){
                mainView.setVisibility(View.GONE);
            }
        }

        return v;
    }

    private void CreateView() {
        rcvLoad = v.findViewById(R.id.rcvLoad);
        btnSave = v.findViewById(R.id.btnSimpan);
        txtKeterangan = v.findViewById(R.id.txtKeterangan);
        btnAdd = v.findViewById(R.id.btnAddItem);
        mainView = v.findViewById(R.id.mainView);
        thisActivity = getActivity();
        SetJenisTampilan();
        pengeluaranLainInputActivity = (PengeluaranLainInputActivity) getActivity();

        pengeluaranLainDetTable = new PengeluaranLainDetTable(thisActivity);
        pengeluaranLainMstTable = new PengeluaranLainMstTable(thisActivity);
        penambahPengurangBarangTable = new PenambahPengurangBarangTable(thisActivity);
    }

    private void InitClass(){
        outletUid = SharedPrefsUtils.getStringPreference(getContext(), "outlet_uid");
        userId = SharedPrefsUtils.getStringPreference(getContext(), "user_id");
        karyawanUid =  SharedPrefsUtils.getStringPreference(getContext(), "karyawan_uid");
        kasbankUid = GlobalTable.getKasbankUid(getContext(),TIPE_BAYAR_TUNAI);
        Nomor = ""; //Sementara dikosongkan
        master_uid = "";
        TipeInput = JConst.TRANS_PENGELUARAN_LAIN_BARANG;
    }

    private void SetJenisTampilan() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(thisActivity);
        rcvLoad.setLayoutManager(linearLayoutManager);

        mAdapter = new PengeluaranLainBarangAdapter(this, ListItems, R.layout.list_item_pengeluaran_barang);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
        mAdapter.addModel(new PengeluaranLainDetModel());
    }

    private void EventClass(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PengeluaranLainDetModel data = new PengeluaranLainDetModel();
                mAdapter.addModel(data);
                adapterPos = mAdapter.getItemCount() - 1;
                mAdapter.notifyItemInserted(adapterPos);
                rcvLoad.scrollToPosition(adapterPos); // Auto-scroll to the last item

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSave.setEnabled(false);
                if (ListItems.size() > 0) {
                    if (isValid()) {
                        Runnable runPositive = new Runnable() {
                            @Override
                            public void run() {
                                save();
                                btnSave.setEnabled(true);
                            }
                        };
                        Runnable runNegative = new Runnable() {
                            @Override
                            public void run() {
                                btnSave.setEnabled(true);
                            }
                        };
                        ShowDialog.confirmDialog(thisActivity, "Konfirmasi", "Apakah data sudah benar?", "Ya", "Tidak", runPositive, runNegative);
                    }else{
                        btnSave.setEnabled(true);
                    }
                } else {
                    btnSave.setEnabled(true);
                    Snackbar.make(view, "Tidak ada data di input.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }
    private boolean isValid(){
        int i = 0; // Mulai dari 0
        while (i < ListItems.size()) {
            if (ListItems.get(i).getBarangUid().equals("")) {
                if (ListItems.size() > 1) {
                    mAdapter.removeModel(i);  // Menghapus item
                    // Tidak perlu menambah 'i' di sini karena ukuran list berkurang
                } else {
                    i++;  // Hanya tambahkan 'i' jika tidak ada item yang dihapus
                }
            } else {
                i++;  // Hanya tambahkan 'i' jika tidak ada item yang dihapus
            }
        }
        for (i = 0; i < ListItems.size(); i++) {
            if (ListItems.get(i).getBarangUid().equals("")){
//                Snackbar.make(thisActivity.findViewById(android.R.id.content), "Barang pada baris " + (i + 1) + " belum dipilih.", Snackbar.LENGTH_SHORT).show();
                ShowDialog.infoDialog(thisActivity, "Informasi","Barang pada baris " + (i + 1) + " belum dipilih." );
                return false;
            }
            if (ListItems.get(i).getQty() == 0){
//                Snackbar.make(thisActivity.findViewById(android.R.id.content), "Qty pada baris " + (i + 1) + " belum diisi.", Snackbar.LENGTH_SHORT).show();
                ShowDialog.infoDialog(thisActivity, "Informasi","Qty pada baris " + (i + 1) + " belum diisi.");
                return false;
            }
            for (int j = i+1; j < ListItems.size(); j++) {
                if (ListItems.get(i).getBarangUid().equals(ListItems.get(j).getBarangUid())){
//                    Snackbar.make(thisActivity.findViewById(android.R.id.content), "Barang pada baris " + (j + 1) + " sama dengan baris " + (i+1) + ".", Snackbar.LENGTH_SHORT).show();
                    ShowDialog.infoDialog(thisActivity, "Informasi","Barang pada baris " + (j + 1) + " sama dengan baris " + (i+1) + ".");
                    return false;
                }
            }
        }
        if (txtKeterangan.getText().toString().trim().isEmpty()) {
            ShowDialog.infoDialog(thisActivity, "Informasi","Keterangan harus diisi." );
            txtKeterangan.requestFocus();
            return false;
        }
        return true;
    }

    private boolean save() {
        SQLiteDatabase db = JApplication.getInstance().db;
        db.beginTransaction(); // Mulai transaksi

        PengeluaranLainMstTable pengeluaranLainMstTable = new PengeluaranLainMstTable(thisActivity, db);
        PengeluaranLainDetTable pengeluaranLainDetTable = new PengeluaranLainDetTable(thisActivity, db);
        PenambahPengurangBarangTable penambahPengurangBarangTable = new PenambahPengurangBarangTable(thisActivity, db);
        try {
            // save master
            String mstUid = UUID.randomUUID().toString();

            PengeluaranLainMstModel modelMst = new PengeluaranLainMstModel();
            modelMst.setUid(mstUid);
            modelMst.setOutletUid(outletUid);
            modelMst.setUserId(userId);
            modelMst.setNomor(pengeluaranLainInputActivity.Nomor);
            modelMst.setTanggal(Global.serverNowLong());
            modelMst.setKeterangan(txtKeterangan.getText().toString().trim());
            modelMst.setTipe(JConst.TRANS_PENGELUARAN_LAIN_BARANG);
            modelMst.setKaryawanUid(karyawanUid);
            modelMst.setLastUpdate(Global.serverNowLong());
            modelMst.setTglInput(Global.serverNowLong());

            // save master
            pengeluaranLainMstTable.insert(modelMst);

            // Save Detail dan Penambah Pengurang
            StringBuilder barangUidIn = new StringBuilder();
            for (PengeluaranLainDetModel item : ListItems) {
                String detUid = UUID.randomUUID().toString();
                String ppUid = UUID.randomUUID().toString();

                // save detail
                PengeluaranLainDetModel modelDet = new PengeluaranLainDetModel();
                modelDet.setUid(detUid);
                modelDet.setMasterUid(mstUid);
                modelDet.setBarangUid(item.getBarangUid());
                modelDet.setSatuanUid(item.getSatuanUid());
                modelDet.setQty(item.getQty());
                modelDet.setQtyPrimer(item.getQtyPrimer());
                modelDet.setKonversi(item.getKonversi());
                modelDet.setKasBankUid(item.getKasBankUid());
                modelDet.setJumlah(item.getJumlah());
                modelDet.setKeterangan(item.getKeterangan());
                pengeluaranLainDetTable.insert(modelDet);

                // save penambah pengurang
                PenambahPengurangBarangModel modelPP = new PenambahPengurangBarangModel();
                modelPP.setUid(ppUid);
                modelPP.setTransUid(mstUid);
                modelPP.setTanggal(Global.serverNowLong());
                modelPP.setNomor(pengeluaranLainInputActivity.Nomor);
                modelPP.setOutletUid(outletUid);
                modelPP.setUserId(userId);
                modelPP.setTipeTrans(JConst.TRANS_PENGELUARAN_LAIN_BARANG);
                modelPP.setBarangUid(item.getBarangUid());
                modelPP.setQtyPrimer(-item.getQtyPrimer());
                penambahPengurangBarangTable.insert(modelPP);

                // Simpan barangUid untuk pengecekan barang minus
                if (barangUidIn.length() > 0) {
                    barangUidIn.append(", '").append(item.getBarangUid()).append("'");
                } else {
                    barangUidIn.append("'").append(item.getBarangUid()).append("'");
                }
            }

            // 🔥 Cek barang minus sebelum commit transaksi
            String barangMinus = penambahPengurangBarangTable.cekBarangMinus(barangUidIn.toString(), outletUid, Global.serverNowLong());
            if (!barangMinus.equals("")) {
                ShowDialog.infoDialog(thisActivity, "Informasi", barangMinus);
                return false; // Langsung keluar, rollback otomatis
            }

            // ✅ Commit transaksi jika tidak ada barang minus
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction(); // Jika tidak commit, maka rollback otomatis
        }

        // Setelah transaksi sukses, lanjutkan
        Intent intent = thisActivity.getIntent();
        intent.putExtra("uuid", uuid);
        btnSave.setEnabled(true);
        thisActivity.setResult(RESULT_OK, intent);
        thisActivity.finish();
        return true;
    }

    /*private boolean save(){
        //cek
        StringBuilder barangUidIn = new StringBuilder();
        for (PengeluaranLainDetModel item : ListItems) {
            if (barangUidIn.toString().equals("")) {
                barangUidIn.append("'").append(item.getBarangUid()).append("'");
            }else {
                barangUidIn.append(", '").append(item.getBarangUid()).append("'");
            }
        }
        String barangMinus = penambahPengurangBarangTable.cekBarangMinus(barangUidIn.toString(), outletUid, Global.serverNowLong());
        if (!barangMinus.equals("")){
            ShowDialog.infoDialog(thisActivity, "Informasi",barangMinus);
            return false;
        }

        // save master
        String mstUid = UUID.randomUUID().toString();
        String detUid = UUID.randomUUID().toString();
        String ppUid = UUID.randomUUID().toString();

        PengeluaranLainMstModel modelMst = new PengeluaranLainMstModel();
        modelMst.setUid(mstUid);
        modelMst.setOutletUid(outletUid);
        modelMst.setUserId(userId);
        modelMst.setNomor(pengeluaranLainInputActivity.Nomor);
        modelMst.setTanggal(Global.serverNowLong());
        modelMst.setKeterangan(txtKeterangan.getText().toString().trim());
        modelMst.setTipe(JConst.TRANS_PENGELUARAN_LAIN_BARANG);
        modelMst.setKaryawanUid(karyawanUid);
        modelMst.setLastUpdate(Global.serverNowLong());
        modelMst.setTglInput(Global.serverNowLong());
        //save
        pengeluaranLainMstTable.insert(modelMst);

        // Save Detail
        for (PengeluaranLainDetModel item : ListItems) {
            // save detail
            PengeluaranLainDetModel modelDet = new PengeluaranLainDetModel();
            modelDet.setUid(detUid);
            modelDet.setMasterUid(mstUid);
            modelDet.setBarangUid(item.getBarangUid());
            modelDet.setSatuanUid(item.getSatuanUid());
            modelDet.setQty(item.getQty());
            modelDet.setQtyPrimer(item.getQtyPrimer());
            modelDet.setKonversi(item.getKonversi());
            modelDet.setKasBankUid(item.getKasBankUid());
            modelDet.setJumlah(item.getJumlah());
            modelDet.setKeterangan(item.getKeterangan());
            //save
            pengeluaranLainDetTable.insert(modelDet);

            // save penambah pengurang
            PenambahPengurangBarangModel modelPP = new PenambahPengurangBarangModel();
            modelPP.setUid(ppUid);
            modelPP.setTransUid(mstUid);
            modelPP.setTanggal(Global.serverNowLong());
            modelPP.setNomor(pengeluaranLainInputActivity.Nomor);
            modelPP.setOutletUid(outletUid);
            modelPP.setUid(userId);
            modelPP.setTipeTrans(JConst.TRANS_PENGELUARAN_LAIN_BARANG);
            modelPP.setBarangUid(item.getBarangUid());
            modelPP.setQtyPrimer(-item.getQtyPrimer());
            //save
            penambahPengurangBarangTable.insert(modelPP);
        }

        Intent intent = thisActivity.getIntent();
        intent.putExtra("uuid", uuid);
        btnSave.setEnabled(true);
        thisActivity.setResult(RESULT_OK, intent);
        thisActivity.finish();
        return true;
    }*/
/*
    private boolean save() {
        JSONObject params = createPengeluaranLainRequestBody();
        String url = Routes.url_transaksi() + "pengeluaran_lain_mst/save";
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params, response -> {
            try {
                if (response != null) {
                    // Response sudah berbentuk JSONObject, tidak perlu parsing ulang
                    JSONObject dataObject = response.getJSONObject("data");
                    uuid = dataObject.getString("uid_pengeluaran_lain_mst");

                    Intent intent = thisActivity.getIntent();
                    intent.putExtra("uuid", uuid);
                    btnSave.setEnabled(true);
                    thisActivity.setResult(RESULT_OK, intent);
                    thisActivity.finish();
                }
            } catch (Exception e) {
                btnSave.setEnabled(true);
                e.printStackTrace();
                Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            btnSave.setEnabled(true);
            ApiHelper.handleError(thisActivity, error, () -> save());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", SharedPrefsUtils.getStringPreference(getContext(), "api_key"));
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);

        return true;
    }
*/
    private JSONObject createPengeluaranLainRequestBody() {
        JSONObject paramsMst = new JSONObject();
        JSONArray pengeluaranLainDetArray = new JSONArray();
        JSONArray penambahPengurangArray = new JSONArray();
        String tanggal = FungsiGeneral.getTgljamFormatMySql(Global.serverNowLong());

        try {
            // Menambahkan data ke paramsMst
            paramsMst.put("outlet_uid", outletUid);
            paramsMst.put("user_id", userId);
            paramsMst.put("nomor", pengeluaranLainInputActivity.Nomor);
            paramsMst.put("tanggal", tanggal);
            paramsMst.put("keterangan", txtKeterangan.getText().toString().trim());
            paramsMst.put("tipe", JConst.TRANS_PENGELUARAN_LAIN_BARANG);
            paramsMst.put("karyawan_uid", karyawanUid);

            // Mengisi array pengeluaran_lain_det
            for (PengeluaranLainDetModel item : ListItems) {
                JSONObject paramsDet = new JSONObject();
                paramsDet.put("barang_uid", item.getBarangUid());
                paramsDet.put("satuan_uid", item.getSatuanUid());
                paramsDet.put("qty", item.getQty());
                paramsDet.put("qty_primer", item.getQtyPrimer());
                paramsDet.put("konversi", item.getKonversi());
                paramsDet.put("kas_bank_uid", "");
                paramsDet.put("jumlah", 0);
                paramsDet.put("keterangan", "");
                pengeluaranLainDetArray.put(paramsDet);

                JSONObject paramsPP = new JSONObject();
                paramsPP.put("tanggal", tanggal);
                paramsPP.put("nomor", pengeluaranLainInputActivity.Nomor);
                paramsPP.put("outlet_uid", outletUid);
                paramsPP.put("user_id", userId);
                paramsPP.put("tipe_trans", JConst.TRANS_PENGELUARAN_LAIN_BARANG);
                paramsPP.put("barang_uid", item.getBarangUid());
                paramsPP.put("qty_primer", -item.getQtyPrimer());
                paramsPP.put("keterangan", item.getKeterangan().trim());
                penambahPengurangArray.put(paramsPP);
            }

            // Menyusun objek utama
            JSONObject params = new JSONObject();
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("pengeluaran_lain_mst", paramsMst);
            params.put("pengeluaran_lain_det", pengeluaranLainDetArray);

            if (TipeInput.equals(JConst.TRANS_PENGELUARAN_LAIN_BARANG)) {
                params.put("penambah_pengurang_barang", penambahPengurangArray);
            }

            return params;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JApplication.currentActivity = thisActivity;
        if (requestCode == REQUEST_FILTER_BARANG && resultCode == RESULT_OK) {
            // Handle the result from LovBarangActivity
            if (data != null) {
                Bundle extra = data.getExtras();
                namaBrg = extra.getString("barang_nama");
                uidBrg = extra.getString("barang_uid");
                konversi = extra.getDouble("konversi_primer");
                uidSatuan = extra.getString("satuan_primer_uid");
                namaSatuan = extra.getString("nama_satuan_primer");

                mAdapter.Datas.get(adapterPos).setBarang(namaBrg);
                mAdapter.Datas.get(adapterPos).setBarangUid(uidBrg);
                mAdapter.Datas.get(adapterPos).setKonversi(konversi);
                mAdapter.Datas.get(adapterPos).setSatuanUid(uidSatuan);
                mAdapter.Datas.get(adapterPos).setSatuan(namaSatuan);

                getstok_program(uidBrg);
                mAdapter.notifyItemChanged(adapterPos);
            }
        } else {
            // Handle the result from LovBarangActivity
            if (data != null) {
                Bundle extra = data.getExtras();
                master_uid = extra.getString("uid");
                outletUid = extra.getString("outlet_uid");
//                pengeluaranLainInputActivity.LoadData();
//                mAdapter.Datas.get(adapterPos).setqty_program(stokProgram);
//                mAdapter.notifyItemChanged(adapterPos);
            }

        }
    }

    private void getstok_program(String uidBrg) {
        JSONObject params = new JSONObject();
        try {
            params.put("barang_uid", uidBrg);
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("outlet_uid", outletUid);
            String tanggal = FungsiGeneral.getTglFormatMySql(FungsiGeneral.serverNowLong());
            params.put("tanggal",tanggal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Routes.url_get_stok();//" Filter;
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                stokProgram = obj.getDouble("saldoakhir");
                                namaSatuan = obj.getString("satuan");
                                konversi = obj.getDouble("qty_primer");
                                mAdapter.Datas.get(adapterPos).setQtyPrimer(stokProgram);
                                mAdapter.Datas.get(adapterPos).setSatuan(namaSatuan);
                                mAdapter.Datas.get(adapterPos).setKonversi(konversi);
                                mAdapter.notifyItemChanged(adapterPos);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(thisActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                },
                error -> {
                    ApiHelper.handleError(thisActivity, error, () -> getstok_program(uidBrg));
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Api-key", SharedPrefsUtils.getStringPreference(getContext(), "api_key"));
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private void LoadData(){
        mAdapter.removeAllModel();
        ListItems = pengeluaranLainDetTable.getRecords(pengeluaranLainInputActivity.master_uid);
        if(ListItems.size() > 0){
            mAdapter.addModels(ListItems);
        }
    }
    /*
    private void LoadData() {
        JSONObject params = new JSONObject();
        try {
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("master_uid", pengeluaranLainInputActivity.master_uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Routes.url_transaksi() + "pengeluaran_lain_det/get";
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    List<PengeluaranLainDetModel> itemDataModels = new ArrayList<>();
                    itemDataModels.clear();
                    JSONArray jsonArray = response.optJSONArray("data");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (obj.optInt("seq",0) > 0) {
                                    PengeluaranLainDetModel Data = new PengeluaranLainDetModel(
                                            obj.getString("barang_uid"),
                                            obj.getString("satuan_uid"),
                                            obj.getString("keterangan"),
                                            obj.getString("nama_barang"),
                                            obj.getString("nama_satuan"),
                                            obj.getDouble("qty"),
                                            obj.getDouble("konversi"),
                                            obj.getDouble("qty_primer"),
                                            obj.getString("kas_bank_uid"),
                                            obj.getDouble("jumlah")
                                    );

                                    itemDataModels.add(Data);
                                    txtKeterangan.setText(obj.getString("keterangan_mst"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(thisActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    mAdapter.removeAllModel();
                    mAdapter.addModels(itemDataModels);
                },
                error -> {
                    ApiHelper.handleError(thisActivity, error, () -> LoadData());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", SharedPrefsUtils.getStringPreference(thisActivity, "api_key"));
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }
    */
}
