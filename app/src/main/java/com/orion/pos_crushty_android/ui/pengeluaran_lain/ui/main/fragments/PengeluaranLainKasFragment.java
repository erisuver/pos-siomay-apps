package com.orion.pos_crushty_android.ui.pengeluaran_lain.ui.main.fragments;

import static android.app.Activity.RESULT_OK;
import static com.orion.pos_crushty_android.globals.JConst.TIPE_BAYAR_TUNAI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.R;
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
import com.orion.pos_crushty_android.ui.pengeluaran_lain.PengeluaranLainInputActivity;
import com.orion.pos_crushty_android.utility.TextWatcherUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class PengeluaranLainKasFragment extends Fragment {
    private View v;
    private Button btnSave;
    private String uuid;
    private Activity thisActivity;
    final int REQUEST_FILTER_BARANG = 1;
    private String karyawanUid, kodeOutlet, TipeInput, kasbankUid, masterUid, outletUid, Nomor, userId;
    private TextInputEditText txtKeterangan, txtJumlah;
    private String namaBrg;
    private String uidBrg;
    private double stokProgram;
    ImageButton btnAdd;
    PengeluaranLainInputActivity pengeluaranLainInputActivity;
    View mainView;
    private PengeluaranLainDetTable pengeluaranLainDetTable;
    private PengeluaranLainMstTable pengeluaranLainMstTable;
    private PenambahPengurangBarangTable penambahPengurangBarangTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kas, container, false);
        v = view;
        JApplication.currentActivity = getActivity();
        thisActivity = getActivity();
        pengeluaranLainInputActivity = (PengeluaranLainInputActivity) getActivity();

        CreateView();
        InitClass();
        EventClass();

        if (pengeluaranLainInputActivity.mode != null && pengeluaranLainInputActivity.mode.equals("detail")) {
            LoadData();
            btnSave.setVisibility(View.GONE);
            setEnabledDisabledComponent(false);
            if (!pengeluaranLainInputActivity.tipeInputMst.equals(TipeInput)){
                mainView.setVisibility(View.GONE);
            }
        }
        return v;
    }

    private void CreateView() {
        btnSave = v.findViewById(R.id.btnSimpanPLK);
        txtKeterangan = v.findViewById(R.id.txtKeteranganPLK);
        txtJumlah = v.findViewById(R.id.txtJumlah);
        mainView = v.findViewById(R.id.mainView);
        pengeluaranLainDetTable = new PengeluaranLainDetTable(thisActivity);
        pengeluaranLainMstTable = new PengeluaranLainMstTable(thisActivity);
        penambahPengurangBarangTable = new PenambahPengurangBarangTable(thisActivity);
    }

    private void InitClass() {
        txtJumlah.setText(Global.FloatToStrFmt(0));
        txtJumlah.addTextChangedListener(TextWatcherUtils.formatAmountTextWatcher(txtJumlah));
        outletUid = SharedPrefsUtils.getStringPreference(getContext(), "outlet_uid");
        userId = SharedPrefsUtils.getStringPreference(getContext(), "user_id");
        karyawanUid = SharedPrefsUtils.getStringPreference(getContext(), "karyawan_uid");
        kasbankUid = GlobalTable.getKasbankUid(getContext(), TIPE_BAYAR_TUNAI);

        TipeInput = JConst.TRANS_PENGELUARAN_LAIN_KAS;
    }

    private void EventClass() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSave.setEnabled(false);
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
            }
        });

    }

    private void setEnabledDisabledComponent(boolean isEnabled) {
        Global.setEnabledTextInputEditText(txtKeterangan, isEnabled);
        Global.setEnabledTextInputEditText(txtJumlah, isEnabled);
    }
    private boolean isValid(){
        if (Global.StrFmtToFloat(txtJumlah.getText().toString()) == 0){
//            Snackbar.make(thisActivity.findViewById(android.R.id.content), "Jumlah tidak boleh 0.", Snackbar.LENGTH_SHORT).show();
            ShowDialog.infoDialog(thisActivity, "Informasi","Jumlah tidak boleh 0." );
            txtJumlah.requestFocus();
            return false;
        }
        if (txtKeterangan.getText().toString().trim().isEmpty()) {
            ShowDialog.infoDialog(thisActivity, "Informasi","Keterangan harus diisi." );
            txtKeterangan.requestFocus();
            return false;
        }
        return true;
    }
/*

    private boolean save() {
        JSONObject params = createJsonObjectPenerimaanLain();
        String url = Routes.url_transaksi() + "pengeluaran_lain_mst/save";
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, params, response -> {
            try {
                if (response != null) {
                    // Response sudah berbentuk JSONObject, tidak perlu parsing ulang
                    JSONObject dataObject = response.getJSONObject("data");
                    uuid = dataObject.getString("uid_pengeluaran_lain_mst");

                    Intent intent = thisActivity.getIntent();
                    intent.putExtra("uuid", uuid);
                    thisActivity.setResult(RESULT_OK, intent);
                    btnSave.setEnabled(true);
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
                headers.put("Api-key", SharedPrefsUtils.getStringPreference(getContext(), "api_key"));
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);

        return true;
    }
*/

    private boolean save(){
        String mstUid = UUID.randomUUID().toString();
        String detUid = UUID.randomUUID().toString();

        // save master
        PengeluaranLainMstModel modelMst = new PengeluaranLainMstModel();
        modelMst.setUid(mstUid);
        modelMst.setOutletUid(outletUid);
        modelMst.setUserId(userId);
        modelMst.setNomor(pengeluaranLainInputActivity.Nomor);
        modelMst.setTanggal(Global.serverNowLong());
        modelMst.setKeterangan(txtKeterangan.getText().toString().trim());
        modelMst.setTipe(TipeInput);
        modelMst.setKaryawanUid(karyawanUid);
        modelMst.setLastUpdate(Global.serverNowLong());
        modelMst.setTglInput(Global.serverNowLong());
        //save
        pengeluaranLainMstTable.insert(modelMst);

        // save detail
        PengeluaranLainDetModel modelDet = new PengeluaranLainDetModel();
        modelDet.setUid(detUid);
        modelDet.setMasterUid(mstUid);
        modelDet.setKasBankUid(kasbankUid);
        modelDet.setJumlah(Global.StrFmtToFloat(txtJumlah.getText().toString()));
        modelDet.setKeterangan(txtKeterangan.getText().toString().trim());
        //save
        pengeluaranLainDetTable.insert(modelDet);

        Intent intent = thisActivity.getIntent();
        intent.putExtra("uuid", uuid);
        btnSave.setEnabled(true);
        thisActivity.setResult(RESULT_OK, intent);
        thisActivity.finish();
        return true;
    }

    private JSONObject createJsonObjectPenerimaanLain() {
        JSONObject params = new JSONObject();
        try {
            // Parameter Master
            JSONObject paramsMst = new JSONObject();
            String tanggal = FungsiGeneral.getTgljamFormatMySql(Global.serverNowLong());
            paramsMst.put("outlet_uid", outletUid);
            paramsMst.put("user_id", userId);
            paramsMst.put("nomor", pengeluaranLainInputActivity.Nomor);
            paramsMst.put("tanggal", tanggal);
            paramsMst.put("keterangan", txtKeterangan.getText().toString().trim());
            paramsMst.put("tipe", TipeInput);
            paramsMst.put("karyawan_uid", karyawanUid);

            // Parameter Detail
            JSONArray pengeluaranLainDetArray = new JSONArray();
            JSONObject paramsDet = new JSONObject();
            paramsDet.put("barang_uid", "");
            paramsDet.put("satuan_uid", "");
            paramsDet.put("qty", 0);
            paramsDet.put("qty_primer", 0);
            paramsDet.put("konversi",0);
            paramsDet.put("keterangan", txtKeterangan.getText().toString().trim());
            paramsDet.put("kas_bank_uid", kasbankUid);
            paramsDet.put("jumlah", Global.StrFmtToFloat(txtJumlah.getText().toString()));
            pengeluaranLainDetArray.put(paramsDet);


            // Membuat JSON akhir
            params.put("tipe_apps", JConst.TIPE_APPS_ANDROID);
            params.put("pengeluaran_lain_mst", paramsMst);
            params.put("pengeluaran_lain_det", pengeluaranLainDetArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params;
    }

    private void LoadData(){
        PengeluaranLainDetModel data = pengeluaranLainDetTable.getRecordByUid(pengeluaranLainInputActivity.master_uid);
        if (data != null) {
            txtJumlah.setText(Global.FloatToStrFmt(data.getJumlah()));
            txtKeterangan.setText(data.getKeterangan());
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
                                            obj.getString("kode_barang"),
                                            obj.getString("nama_barang"),
                                            obj.getDouble("qty"),
                                            obj.getDouble("konversi"),
                                            obj.getDouble("qty_primer"),
                                            obj.getString("satuan_primer_uid"),
                                            obj.getDouble("jumlah")
                                    );

                                    itemDataModels.add(Data);
                                    txtJumlah.setText(Global.FloatToStrFmt(Data.getJumlah()));
                                    txtKeterangan.setText(Data.getKeterangan());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(thisActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
//                    ListItems = itemDataModels;
//                    mAdapter.removeAllModel();
//                    mAdapter.addModels(ListItems);
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
