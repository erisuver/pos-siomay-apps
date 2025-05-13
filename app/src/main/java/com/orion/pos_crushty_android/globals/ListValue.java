package com.orion.pos_crushty_android.globals;

import com.orion.pos_crushty_android.databases.master_outlet.MasterOutletModel;

import java.util.ArrayList;
import java.util.List;

public class ListValue {

    public static List<String> list_table_sqlite() {
        List<String> list = new ArrayList<>();
        list.add("versi_data");
        list.add("login");
        list.add("system_user");
        list.add("master_outlet");
        list.add("master_barang");
        list.add("master_barang_jual");
        list.add("detail_barang_jual");
        list.add("master_satuan");
        list.add("master_karyawan");
        list.add("setting_komposisi_mst");
        list.add("setting_komposisi_det");
        list.add("penambah_pengurang_barang");
        /* tutup 080425 hanya tarik data master
        list.add("penjualan_mst");
        list.add("penjualan_det");
        list.add("penjualan_det_barang");
        list.add("penjualan_bayar");
        list.add("pengeluaran_lain_mst");
        list.add("pengeluaran_lain_det");
         */

        return list;
    }

    public static ArrayList<String> list_table_sync() {
        ArrayList<String> list = new ArrayList<>();
        list.add("system_user");
        list.add("master_outlet");
        list.add("master_barang");
        list.add("master_barang_jual");
        list.add("detail_barang_jual");
        list.add("master_satuan");
        list.add("master_karyawan");
        list.add("setting_komposisi_mst");
        list.add("setting_komposisi_det");
        list.add("penambah_pengurang_barang");
        /* tutup 080425 hanya tarik data master
        list.add("penjualan_mst");
        list.add("penjualan_bayar");
        list.add("penjualan_det");
        list.add("penjualan_det_barang");
        list.add("pengeluaran_lain_mst");
        list.add("pengeluaran_lain_det");
         */

        return list;
    }

    public static List<String> list_outlet(ArrayList<MasterOutletModel> list) {
        List<String> listStringOutlet = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listStringOutlet.add(list.get(i).getNama());
        }
        return listStringOutlet;
    }

    public static List<String> list_outlet_uid(ArrayList<MasterOutletModel> list) {
        List<String> listStringOutlet = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listStringOutlet.add(list.get(i).getUid());
        }
        return listStringOutlet;
    }


}



