package com.orion.pos_crushty_android.globals;

import static com.orion.pos_crushty_android.JApplication.fmt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class FungsiGeneral {

    public static String getTahun(long date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        String thn = format.format(date);
        return thn;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static float StrFmtToFloat(String input){

        final NumberFormat formatter = NumberFormat.getInstance();

        try {
            Number n = formatter.parse(input);
            return n.floatValue();
        }catch(ParseException e){
            return 0;
        }
    }


    public static float StrFmtToFloatInput(String input){

        final NumberFormat formatter = NumberFormat.getInstance();
        String koma = formatter.format(0.1);
        if (koma.contains(",")){
            if (!input.contains(",")) {
                input = input.replace(".", ",");
            }
        }
        try {
            Number n = formatter.parse(input);
            return n.floatValue();
        }catch(ParseException e){
            return 0;
        }
    }

//    public static String FloatToStrFmt(double input){
//
//        double koma, isi;
//        isi = Math.floor(input);
//        koma = input - isi;
//        String hasil1, hasil2;
//        NumberFormat format = NumberFormat.getInstance();
//        hasil1 = format.format(isi);
//        hasil2 = String.format("%.2f", koma);
//
//        return hasil1+hasil2.substring(1);
//    }

    public static String FloatToStrFmt(double input){
        return fmt.format(input);
    }

    public static String FloatToStrFmt(double input, boolean withRpSign){
        if (withRpSign)
            return "Rp. "+fmt.format(input);
        else
            return fmt.format(input);

    }

    public static final String tag_json_obj = "json_obj_req";


    public static String getBulan(long date){
        SimpleDateFormat format = new SimpleDateFormat("MM");
        String bln = format.format(date);
        return bln;
    }

    public static String getHari(long date){
        SimpleDateFormat format = new SimpleDateFormat("dd");
        String hr = format.format(date);
        return hr;
    }

    public static String getTglFormat(long date){
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String getTglFmt(long date, String Format){
        DateFormat formatter = new SimpleDateFormat(Format);
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }


    public static String getTglFormatNonDay(long date){
        DateFormat formatter = new SimpleDateFormat("MM-yyyy");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String getTglFormatBulanTahun(long date){
        DateFormat formatter = new SimpleDateFormat("MMMM yyyy");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String getTglFormatMySql(long date){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String getTgljamFormatMySql(long date){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        return formatter.format(calender.getTime());
    }

    public static String serverNowFormated(){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTimeInMillis());
    }


    public static long serverNowStartOfTheMonthLong(){
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.DAY_OF_MONTH, 1);
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
        return calender.getTimeInMillis();
    }


    public static String serverNowFormatedBulanTahun(){
        DateFormat formatter = new SimpleDateFormat("MMMM yyyy");
        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTimeInMillis());
    }

    public static long serverNowLong(){
        //DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calender = Calendar.getInstance();
        return calender.getTimeInMillis();
    }

    public static String serverNowFormated4Ekspor(){
        DateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmm");
        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTimeInMillis());
    }
    public static String serverNow(){
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTime());
    }
    public static String serverNow4Nomor(){
        DateFormat formatter = new SimpleDateFormat("MMyy");

        Calendar calender = Calendar.getInstance();
        return formatter.format(calender.getTime());
    }

    public static long EndOfTheMonthLong(Long date){
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(date);
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH));
        calender.set(Calendar.HOUR_OF_DAY, calender.getActualMaximum(Calendar.HOUR_OF_DAY));
        calender.set(Calendar.MINUTE, calender.getActualMaximum(Calendar.MINUTE));
        calender.set(Calendar.SECOND, calender.getActualMaximum(Calendar.SECOND));
        calender.set(Calendar.MILLISECOND, calender.getActualMaximum(Calendar.MILLISECOND));
        return calender.getTimeInMillis();
    }


    public static long getMillisDate(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            return date;
        }
        return 0;
    }

    public static long getMillisDateFmt(String input, String fmt) {
        SimpleDateFormat f = new SimpleDateFormat(fmt);
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            return date;
        }
        return 0;
    }


    public static long getMillisDateBulanTahun(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MMMM yyyy");
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            return date;
        }
        return 0;
    }


    public static long getMillisDateTime(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            return date;
        }
        return 0;
    }


    public static void inform(Context context, String pesan, String title){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(pesan)
                .setCancelable(true)
                .setPositiveButton("Ok",null)
                .show();
    }


    public static long getSimpleDate(String input) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
        }
        return date;
    }

    public static boolean Confirm(Context context, String message) {
        final boolean[] answer = new boolean[3];
        answer[1] = false;
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Confirmation");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                answer[0] = true;
                answer[1] = true;
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                answer[0] = false;
                answer[1] = true;
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
        while(answer[1]=true) {
            answer[2]=true;
        }
        return answer[0];
    }

    public static int StrToIntDef(String Input, int Default){
        try {
            return Integer.parseInt(Input);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Default;
        }
    }

    public static int Hasil(String Input, Character Char){
        int Count = 0;

        for (int i=1; i < Input.length(); i++ ){
            if (Input.charAt(i) == Char ){
                i += 1;
            }
        }
        return Count;
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

//        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public static double Round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static Bitmap getRecyclerViewScreenshot(RecyclerView view) {
        int size = view.getAdapter().getItemCount();
        RecyclerView.ViewHolder holder = view.getAdapter().createViewHolder(view, 0);
        view.getAdapter().onBindViewHolder(holder, 0);
        holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
        Bitmap bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), holder.itemView.getMeasuredHeight() * size,
                Bitmap.Config.ARGB_8888);
        Canvas bigCanvas = new Canvas(bigBitmap);
        bigCanvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        int iHeight = 0;
        holder.itemView.setDrawingCacheEnabled(true);
        holder.itemView.buildDrawingCache();
        bigCanvas.drawBitmap(holder.itemView.getDrawingCache(), 0f, iHeight, paint);
        holder.itemView.setDrawingCacheEnabled(false);
        holder.itemView.destroyDrawingCache();
        iHeight += holder.itemView.getMeasuredHeight();
        for (int i = 1; i < size; i++) {
            view.getAdapter().onBindViewHolder(holder, i);
            holder.itemView.setDrawingCacheEnabled(true);
            holder.itemView.buildDrawingCache();
            bigCanvas.drawBitmap(holder.itemView.getDrawingCache(), 0f, iHeight, paint);
            iHeight += holder.itemView.getMeasuredHeight();
            holder.itemView.setDrawingCacheEnabled(false);
            holder.itemView.destroyDrawingCache();
        }
        return bigBitmap;
    }

    public static Bitmap mergeBitmaps(Bitmap bitmap1, Bitmap bitmap2) {
        int width = Math.max(bitmap1.getWidth(), bitmap2.getWidth());
        int height = bitmap1.getHeight() + bitmap2.getHeight();

        Bitmap mergedBitmap = Bitmap.createBitmap(width, height, bitmap1.getConfig());

        Canvas canvas = new Canvas(mergedBitmap);

        // Draw the first bitmap at the top
        canvas.drawBitmap(bitmap1, 0, 0, null);

        // Draw the second bitmap below the first one
        canvas.drawBitmap(bitmap2, 0, bitmap1.getHeight(), null);

        return mergedBitmap;
    }
    public static Bitmap getCardViewScreenshot(CardView cardView) {
        int size = 1; // Assuming only one CardView
        RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(cardView) {
            // Empty ViewHolder implementation
        };

        cardView.measure(View.MeasureSpec.makeMeasureSpec(cardView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        cardView.layout(0, 0, cardView.getMeasuredWidth(), cardView.getMeasuredHeight());

        Bitmap bigBitmap = Bitmap.createBitmap(cardView.getMeasuredWidth(), cardView.getMeasuredHeight() * size,
                Bitmap.Config.ARGB_8888);
        Canvas bigCanvas = new Canvas(bigBitmap);
        bigCanvas.drawColor(Color.WHITE);
        Paint paint = new Paint();

        int iHeight = 0;
        cardView.setDrawingCacheEnabled(true);
        cardView.buildDrawingCache();
        bigCanvas.drawBitmap(cardView.getDrawingCache(), 0f, iHeight, paint);
        cardView.setDrawingCacheEnabled(false);
        cardView.destroyDrawingCache();
        iHeight += cardView.getMeasuredHeight();

        return bigBitmap;
    }

    public static void infoDialogRun(Context context, String message, final Runnable runnable) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                dialog.dismiss();
                runnable.run();
            }
        });
        dialog.show();
    }
    public static long getDateFromMysql(String input) {
        if (input.equals("")) {
            return 0;
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = f.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = 0;
        if (d != null) {
            date = d.getTime();
            return date;
        }
        return 0;
    }
}
