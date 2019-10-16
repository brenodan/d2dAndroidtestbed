package evolution.lancer.breno.riccitestbed.D2DCommunication;

import android.content.Intent;

import evolution.lancer.breno.ricci2lib.ricci.RemoteIntent;

public class RemoteObject {

    private RemoteIntent remoteIntent = null;
    private Intent intent = null;
    private Object object = null;

    public RemoteObject(RemoteIntent remoteIntent) {
        this.remoteIntent = remoteIntent;
    }

    public RemoteObject(Intent intent) {
        this.intent = intent;
    }

    public RemoteObject(Object object) {
        this.object = object;
    }

    public Intent getIntent() {
        return intent;
    }

    public RemoteIntent getRemoteIntent(){
        return remoteIntent;
    }

    public Object getObject(){
        return object;
    }
}
