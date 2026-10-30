package com.nightinvert.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;

/** Liga/desliga a inversão de cores do Android (vale para todos os apps). */
final class NightMode {
    private static final String KEY = "accessibility_display_inversion_enabled";

    private NightMode() {}

    static boolean isOn(Context c) {
        return Settings.Secure.getInt(c.getContentResolver(), KEY, 0) == 1;
    }

    static boolean canControl(Context c) {
        return c.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS)
                == PackageManager.PERMISSION_GRANTED;
    }

    static boolean set(Context c, boolean on) {
        try {
            return Settings.Secure.putInt(c.getContentResolver(), KEY, on ? 1 : 0);
        } catch (SecurityException e) {
            return false;
        }
    }
}
