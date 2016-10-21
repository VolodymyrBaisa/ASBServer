package server.bios.asbserver.client.autostart;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by BIOS on 9/4/2016.
 */
public class StartAceServer {
    private Context context;
    private volatile static StartAceServer startAceServer;
    private static final String PACKAGE_NAME = "org.acestream.media";
    private static final String CLASS = "org.acestream.engine.ContentStartActivity";

    private StartAceServer(Context context){
        this.context = context;
    }

    public static StartAceServer getInstance(){
        return startAceServer;
    }

    public static void init(Context context){

        synchronized (StartAceServer.class) {
            startAceServer = new StartAceServer(context);
        }
    }

    public boolean exist() throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(PACKAGE_NAME, 0);
        if(packageInfo != null) return true;
        return false;
    }

    public void start(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(PACKAGE_NAME, CLASS));
        intent.setAction(Intent.ACTION_VIEW);
        context.startActivity(intent);
    }
}
