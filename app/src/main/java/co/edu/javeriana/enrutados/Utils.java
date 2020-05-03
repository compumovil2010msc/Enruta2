package co.edu.javeriana.enrutados;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Utils {
    public static boolean checkPermission (Activity context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity context, String permission, String reason, int code) {
        int requestingPermission = ContextCompat.checkSelfPermission(context, permission);
        if (requestingPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                ActivityCompat.requestPermissions(
                        context,
                        new String[]{permission},
                        code
                );
            }
        }
    }
}
