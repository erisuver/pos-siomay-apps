package com.orion.pos_crushty_android.globals;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.orion.pos_crushty_android.R;

public class ShowDialog {
    private static AlertDialog dialogDet = null;

    public static void confirmDialog(Activity activity, String title, String content, Runnable runnable) {
        if (dialogDet == null || !dialogDet.isShowing()) {
            LayoutInflater inflaterDetail;
            View alertLayoutDetail;
            inflaterDetail = activity.getLayoutInflater();
            alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_confirm_dialog, null);

            //alert dialog
            final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle("");
            alert.setView(alertLayoutDetail);
            alert.setCancelable(false);
            dialogDet = alert.create();
            dialogDet.setCanceledOnTouchOutside(false);
            dialogDet.show();

            //create view
            final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
            final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
            final Button btnPositive = (Button) alertLayoutDetail.findViewById(R.id.btnPositive);
            final Button btnNegative = (Button) alertLayoutDetail.findViewById(R.id.btnNegative);
            final ImageButton btnClose = alertLayoutDetail.findViewById(R.id.btnClose);

            //init
            txtTitle.setText(title);
            txtContent.setText(content);
            btnPositive.setText("Coba Lagi");
            btnNegative.setText("Keluar");
            btnClose.setVisibility(View.INVISIBLE);

            //event
            btnPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDet.dismiss();
                    runnable.run();
                }
            });
            btnNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finishAffinity();
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDet.dismiss();
                }
            });
            dialogDet.show();
        }
    }

    public static void confirmDialog(Activity activity, String title, String content, String positiveButton, String negativeButton, Runnable runPositive, Runnable runNegative) {
        if (dialogDet == null || !dialogDet.isShowing()) {
            LayoutInflater inflaterDetail;
            View alertLayoutDetail;
            inflaterDetail = activity.getLayoutInflater();
            alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_confirm_dialog, null);

            //alert dialog
            final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle("");
            alert.setView(alertLayoutDetail);
            alert.setCancelable(false);
            dialogDet = alert.create();
            dialogDet.setCanceledOnTouchOutside(false);
            dialogDet.show();

            //create view
            final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
            final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
            final Button btnPositive = alertLayoutDetail.findViewById(R.id.btnPositive);
            final Button btnNegative = alertLayoutDetail.findViewById(R.id.btnNegative);
            final ImageButton btnClose = alertLayoutDetail.findViewById(R.id.btnClose);

            //init
            txtTitle.setText(title);
            txtContent.setText(content);
            btnPositive.setText(positiveButton);
            btnNegative.setText(negativeButton);

            //event
            btnPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runPositive.run();
                    dialogDet.dismiss();
                }
            });
            btnNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runNegative.run();
                    dialogDet.dismiss();
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runNegative.run();
                    dialogDet.dismiss();
                }
            });
            dialogDet.show();
        }
    }

    public static void infoDialog(Activity activity, String title, String content) {
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_info_dialog, null);

        //alert dialog
        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final Button btnOk = (Button) alertLayoutDetail.findViewById(R.id.btnOk);

        //init
        txtTitle.setText(title);
        txtContent.setText(content);

        //event
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }


    public static void infoDialog(Activity activity, String title, String content, Runnable run) {
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = activity.getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_info_dialog, null);

        //alert dialog
        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("");
        alert.setView(alertLayoutDetail);
        alert.setCancelable(false);
        final AlertDialog dialogDet = alert.create();
        dialogDet.setCanceledOnTouchOutside(false);
        dialogDet.show();

        //create view
        final TextView txtTitle = alertLayoutDetail.findViewById(R.id.txtTitle);
        final TextView txtContent = alertLayoutDetail.findViewById(R.id.txtContent);
        final Button btnOk = (Button) alertLayoutDetail.findViewById(R.id.btnOk);

        //init
        txtTitle.setText(title);
        txtContent.setText(content);

        //event
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDet.dismiss();
                run.run();
            }
        });
        dialogDet.show();
    }

}
