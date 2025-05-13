package com.orion.pos_crushty_android.globals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PDFHelper {

    private File mFolder;
    private File mFile;
    private Context mContext;

    public PDFHelper(File folder, Context context) {
        this.mContext = context;
        this.mFolder = folder;

        if(!mFolder.exists())
            mFolder.mkdirs();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveImageToPDF(View title, Bitmap bitmap, String filename) {

        mFile = new File(mFolder, filename + ".pdf");
        if (!mFile.exists()) {
            int height = title.getHeight() + bitmap.getHeight();
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), height, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            title.draw(canvas);

            canvas.drawBitmap(bitmap, null, new Rect(0, title.getHeight(), bitmap.getWidth(),bitmap.getHeight()), null);

            document.finishPage(page);

            try {
                mFile.createNewFile();
                OutputStream out = new FileOutputStream(mFile);
                document.writeTo(out);
                document.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
