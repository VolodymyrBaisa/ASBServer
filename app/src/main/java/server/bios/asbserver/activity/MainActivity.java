package server.bios.asbserver.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import server.bios.asbserver.R;
import server.bios.asbserver._interface.Constants;
import server.bios.asbserver.service.ServerManager;

/**
 * Created by BIOS on 8/29/2016.
 */
public class MainActivity extends AppCompatActivity {
    private Intent serviceIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                openSettingsActivity();
                return true;
            case R.id.menu_user_profile_settings:
                openUserProfileSettingsActivity();
                return true;
            case R.id.menu_quit:
                quit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSettingsActivity() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void openUserProfileSettingsActivity() {
        Intent settingsIntent = new Intent(this, UserProfileSettings.class);
        startActivity(settingsIntent);
    }

    // Service
    private void startService() {
        if (!isServiceRunning()) {
            serviceIntent = new Intent(this, ServerManager.class);
            startService(serviceIntent);
        }
    }

    private void stopService() {
        if (serviceIntent != null) {
            stopService(serviceIntent);
        }
    }

    //Checking is service running
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (Constants.SERVICE_NAME.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //quit - minimise program
    private void quit() {
        stopService();
        Intent quitIntent = new Intent(Intent.ACTION_MAIN);
        quitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        quitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        quitIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(quitIntent);
    }
}
