package server.bios.asbserver.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import server.bios.asbserver.R;

/**
 * Created by BIOS on 9/1/2016.
 */
public class Settings {
    private SharedPreferences sharedPreferences;
    private volatile static Settings settings;

    private Settings(Context context) {
        if (!PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, false)) {
            PreferenceManager.setDefaultValues(context, R.xml.settings, true);
            PreferenceManager.setDefaultValues(context, R.xml.user_profile_settings, true);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Settings getInstance() {
        return settings;
    }

    public static void init(Context context) {
        synchronized (Settings.class) {
            settings = new Settings(context);
        }
    }

    public String getAceStreamIP() {
        return sharedPreferences.getString("key_pref_ace_stream_ip", "");
    }

    public int getAceStreamPort() {
        return Integer.parseInt(sharedPreferences.getString("key_pref_ace_stream_port", ""));
    }

    public int getAceStreamOutVideoPort() {
        return Integer.parseInt(sharedPreferences.getString("key_pref_ace_stream_out_video_port", ""));
    }

    public String getGender() {
        return sharedPreferences.getString("key_pref_ace_stream_gender", "");
    }

    public String getAge() {
        return sharedPreferences.getString("key_pref_ace_stream_age", "");
    }

    public int getASBServerPort() {
        return Integer.parseInt(sharedPreferences.getString("key_pref_asb_server_port", ""));
    }
}
