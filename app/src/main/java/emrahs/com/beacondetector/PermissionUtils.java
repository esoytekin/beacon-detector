package emrahs.com.beacondetector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by emrahsoytekin on 9.12.2017.
 */

public final class PermissionUtils {
    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isAccessCoarseLocationGranted(Context context) {
        return checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean isAccessBluetoothGranted(Context context) {
        return checkPermission(context, Manifest.permission.BLUETOOTH);
    }

    public static boolean isAccessBluetoothAdminGranted(Context context) {
        return checkPermission(context, Manifest.permission.BLUETOOTH_ADMIN);
    }

    public static void requestPermissions(Object o, int permissionId, String... permissions) {
        ActivityCompat.requestPermissions((AppCompatActivity) o, permissions, permissionId);
    }


}
