package server.bios.asbserver.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;

import server.bios.asbserver.R;
import server.bios.asbserver._interface.Constants;
import server.bios.asbserver.activity.MainActivity;
import server.bios.asbserver.bus.BusStation;
import server.bios.asbserver.bus.CloseEvent;
import server.bios.asbserver.bus.NotifMsgEvent;
import server.bios.asbserver.client.autostart.StartAceServer;
import server.bios.asbserver.server.Server;
import server.bios.asbserver.settings.Settings;

/**
 * Created by BIOS on 8/29/2016.
 */
public class ServerManager extends Service {
    private Notification.Builder builder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Settings.init(this);
        StartAceServer.init(this);
        createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BusStation.getBus().register(this);
        new Thread(new RunServer(), getString(R.string.app_name).concat(" thread")).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusStation.getBus().post(new CloseEvent(true));
        BusStation.getBus().unregister(this);
        stopForeground(true);
    }

    private class RunServer implements Runnable {
        @Override
        public void run() {
            Server server = new Server();
            server.start();
            stopSelf();
        }
    }

    //Create notification service
    private void createNotification() {
        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new Notification.Builder(this);
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setSmallIcon(R.drawable.logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        startForeground(Constants.NOTIF_ID, notification);
    }

    //Set new  notification content msg
    @Subscribe
    public void onEvent(NotifMsgEvent msg) {
        builder.setContentText(msg.message);
        Notification notification = builder.build();
        startForeground(Constants.NOTIF_ID, notification);
    }
}
