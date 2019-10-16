package evolution.lancer.breno.riccitestbed.D2DCommunication;

import android.content.Intent;

import evolution.lancer.breno.ricci2lib.ricci.RemoteIntent;

public interface D2DDataExchangeInterface {

    void setRemoteIntent(RemoteIntent remoteIntent);
    RemoteIntent getRemoteIntent();

    void setIntent(Intent intent);
    Intent getIntent();

    void setObject(Object object);
    Object getObject();

}
