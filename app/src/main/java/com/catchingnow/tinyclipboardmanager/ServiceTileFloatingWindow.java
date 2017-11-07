package com.catchingnow.tinyclipboardmanager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

@TargetApi(Build.VERSION_CODES.N)
public class ServiceTileFloatingWindow extends TileService implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "ServiceTile.FloatingWindow";
    private Context context;

    public void onStartListening() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        update();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if ("pref_floating_button_switch".equals(key))
            update();
    }

    private void update() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fwstate = prefs.getBoolean("pref_floating_button_switch", false);
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(fwstate ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.updateTile();
        }
    }

    public void onStopListening() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onClick() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putBoolean("pref_floating_button_switch", !prefs.getBoolean("pref_floating_button_switch", false)).apply();
            context = this;
            CBWatcherService.startCBService(context, true);
            if (!prefs.getBoolean("pref_floating_button_switch", true)) {
            context.stopService(new Intent(context, FloatingWindowService.class));
            } else {
            context.startService(new Intent(context, FloatingWindowService.class));
            }
    }
}
