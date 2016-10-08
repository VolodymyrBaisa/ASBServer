package server.bios.asbserver.client.autostart;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by BIOS on 9/4/2016.
 */
public class StartAceServer {
    private Context context;
    private volatile static StartAceServer startAceServer;

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

    public void start(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("org.acestream.media", "org.acestream.engine.ContentStartActivity"));
        intent.setAction(Intent.ACTION_VIEW);
        context.startActivity(intent);
    }
}
