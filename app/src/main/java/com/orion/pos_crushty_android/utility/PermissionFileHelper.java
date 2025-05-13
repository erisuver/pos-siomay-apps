package com.orion.pos_crushty_android.utility;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class PermissionFileHelper {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public static void requestPermission(Activity activity, String permission, Runnable actionWhenPermissionGranted) {
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            actionWhenPermissionGranted.run();
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Runnable actionWhenPermissionGranted) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                actionWhenPermissionGranted.run();
            }
        }
    }
}

